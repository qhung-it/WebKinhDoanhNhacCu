package controller.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.service.impl.BrandServiceImpl;
import controller.service.impl.CategoryServiceImpl;
import controller.service.impl.InventoryHistoryServiceImpl;
import controller.service.impl.ProductServiceImpl;
import controller.service.intf.BrandService;
import controller.service.intf.CategoryService;
import controller.service.intf.InventoryHistoryService;
import controller.service.intf.ProductService;
import controller.util.ApiUtil;
import model.entity.Brand;
import model.entity.Category;
import model.entity.InventoryHistory;
import model.entity.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/api/products/*")
public class AdminProductServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final BrandService brandService = new BrandServiceImpl();
    private final InventoryHistoryService inventoryHistoryService = new InventoryHistoryServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = productService.getDanhSachSanPham()
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        ApiUtil.ok(resp, list);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Product p = new Product();
            apply(p, ApiUtil.body(req), true);
            productService.themSanPham(p);

            if (p.getQuantity() > 0) {
                InventoryHistory history = new InventoryHistory();
                history.setProduct(p);
                history.setOperation("INITIAL_STOCK");
                history.setQuantityChange(p.getQuantity());
                history.setStockBefore(0);
                history.setStockAfter(p.getQuantity());
                history.setNote("Tồn kho ban đầu khi tạo sản phẩm");

                Object admin = req.getSession(false).getAttribute("adminUser");
                if (admin instanceof Map) {
                    history.setChangedBy(String.valueOf(((Map<?, ?>) admin).get("email")));
                }

                inventoryHistoryService.record(history);
            }

            ApiUtil.json(resp, 201, toMap(p));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        Product p = (id == null) ? null : productService.timSanPham(id);

        if (p == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy sản phẩm");
            return;
        }

        try {
            apply(p, ApiUtil.body(req), false);
            productService.suaSanPham(p);
            ApiUtil.ok(resp, toMap(p));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        Product product = (id == null) ? null : productService.timSanPham(id);

        if (product == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy sản phẩm");
            return;
        }

        try {
            JsonObject body = ApiUtil.body(req);
            int before = product.getQuantity();
            int after = body.get("quantity").getAsInt();

            if (after < 0) {
                throw new IllegalArgumentException("Số lượng tồn không được âm");
            }

            product.setQuantity(after);
            productService.suaSanPham(product);

            if (before != after) {
                InventoryHistory history = new InventoryHistory();
                history.setProduct(product);

                String operation = ApiUtil.str(body, "operation");
                history.setOperation(operation == null ? "ADJUSTMENT" : operation);
                history.setQuantityChange(after - before);
                history.setStockBefore(before);
                history.setStockAfter(after);
                history.setNote(ApiUtil.str(body, "note"));

                Object admin = req.getSession(false).getAttribute("adminUser");
                if (admin instanceof Map) {
                    history.setChangedBy(String.valueOf(((Map<?, ?>) admin).get("email")));
                }

                inventoryHistoryService.record(history);
            }

            ApiUtil.ok(resp, toMap(product));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        } catch (Exception e) {
            ApiUtil.error(resp, 400, "Số lượng tồn không hợp lệ");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || productService.timSanPham(id) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy sản phẩm");
            return;
        }

        productService.xoaSanPham(id);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    private String idFromPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) {
            return null;
        }
        return p.substring(1).split("/")[0];
    }

    private String opt(JsonObject o, String key) {
        String s = ApiUtil.str(o, key);
        return (s == null || s.isEmpty()) ? null : s;
    }

    /** Gán dữ liệu từ JSON vào p. Kiểm tra chi tiết do ProductService làm. */
    private void apply(Product p, JsonObject body, boolean creating) {
        p.setProductName(opt(body, "name"));

        try {
            p.setPrice(body.get("price").getAsBigDecimal());
        } catch (Exception e) {
            throw new IllegalArgumentException("Giá bán không hợp lệ");
        }

        BigDecimal old = null;
        if (body.has("oldPrice") && !body.get("oldPrice").isJsonNull()) {
            try {
                old = body.get("oldPrice").getAsBigDecimal();
            } catch (Exception e) {
                throw new IllegalArgumentException("Giá gốc không hợp lệ");
            }
            if (old.signum() == 0) {
                old = null;
            }
        }
        p.setOldPrice(old);

        // Tồn kho chỉ được đặt khi tạo mới; sau đó chỉnh ở trang Kho hàng
        if (creating) {
            try {
                p.setQuantity(body.get("quantity").getAsInt());
            } catch (Exception e) {
                throw new IllegalArgumentException("Số lượng tồn không hợp lệ");
            }
        }

        String catId = opt(body, "categoryId");
        String brandId = opt(body, "brandId");

        p.setCategory(catId == null ? null : categoryService.timDanhMuc(catId));
        p.setBrand(brandId == null ? null : brandService.timThuongHieu(brandId));

        p.setBadge(opt(body, "badge"));
        p.setEmoji(opt(body, "emoji"));
        p.setShortDescription(opt(body, "shortDescription"));
        p.setDescription(opt(body, "description"));
        p.setImage(opt(body, "image"));
        p.setIsNew(body.has("isNew") && body.get("isNew").getAsBoolean());
        p.setActive(!body.has("active") || body.get("active").getAsBoolean());

        if (body.has("images") && body.get("images").isJsonArray()) {
            JsonArray arr = body.getAsJsonArray("images");
            List<String> l = new ArrayList<>();
            for (JsonElement e : arr) {
                if (!e.getAsString().trim().isEmpty()) {
                    l.add(e.getAsString().trim());
                }
            }
            p.setImages(l.isEmpty() ? null : String.join("\n", l));
        }
    }

    private Map<String, Object> toMap(Product p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getProductId());
        m.put("sku", p.getSku());
        m.put("name", p.getProductName());
        m.put("shortDescription", p.getShortDescription());
        m.put("description", p.getDescription());
        m.put("price", p.getPrice());
        m.put("oldPrice", p.getOldPrice());
        m.put("quantity", p.getQuantity());
        m.put("badge", p.getBadge());
        m.put("emoji", p.getEmoji());
        m.put("isNew", p.getIsNew() != null && p.getIsNew());
        m.put("active", p.getActive() == null || p.getActive());
        m.put("image", p.getImage());

        List<String> imgs = new ArrayList<>();
        if (p.getImages() != null) {
            for (String s : p.getImages().split("\n")) {
                if (!s.trim().isEmpty()) {
                    imgs.add(s.trim());
                }
            }
        }
        m.put("images", imgs);

        Category c = p.getCategory();
        Brand b = p.getBrand();

        m.put("categoryId", c == null ? null : c.getCategoryId());
        m.put("categoryName", c == null ? "" : c.getCategoryName());
        m.put("brandId", b == null ? null : b.getBrandId());
        m.put("brandName", b == null ? "" : b.getBrandName());

        return m;
    }
}