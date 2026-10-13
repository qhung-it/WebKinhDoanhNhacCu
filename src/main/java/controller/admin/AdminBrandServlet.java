package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.BrandServiceImpl;
import controller.service.impl.ProductServiceImpl;
import controller.service.intf.BrandService;
import controller.service.intf.ProductService;
import controller.util.ApiUtil;
import model.entity.Brand;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/admin/api/brands/*")
public class AdminBrandServlet extends HttpServlet {

    private final BrandService brandService = new BrandServiceImpl();
    private final ProductService productService = new ProductServiceImpl();

    private int countProducts(String brandId) {
        List<?> l = productService.getSanPhamTheoThuongHieu(brandId);
        return l == null ? 0 : l.size();
    }

    // ---- CHỈ SỬA 2 HÀM NÀY nếu tên getter/setter của Brand khác ----
    private Map<String, Object> toMap(Brand b) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", b.getBrandId());
        m.put("name", b.getBrandName());
        m.put("origin", b.getOrigin());
        m.put("desc", b.getDescription());
        m.put("productCount", countProducts(b.getBrandId()));
        return m;
    }

    private void apply(Brand b, JsonObject body) {
        b.setBrandName(ApiUtil.str(body, "name"));
        b.setOrigin(ApiUtil.str(body, "origin"));
        b.setDescription(ApiUtil.str(body, "desc"));
    }
    // ------------------------------------------------------------------

    private String idFromPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) return null;
        return p.substring(1).split("/")[0];
    }

    private boolean nameExists(String name, String exceptId) {
        for (Brand b : brandService.getDanhSachThuongHieu()) {
            if (b.getBrandName() != null && b.getBrandName().equalsIgnoreCase(name)
                    && !b.getBrandId().equals(exceptId)) return true;
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = brandService.getDanhSachThuongHieu()
                .stream().map(this::toMap).collect(Collectors.toList());
        ApiUtil.ok(resp, list);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = ApiUtil.body(req);
        String name = ApiUtil.str(body, "name");
        if (name == null || name.length() < 2) { ApiUtil.error(resp, 400, "Nhập tên thương hiệu"); return; }
        if (nameExists(name, null)) { ApiUtil.error(resp, 409, "Thương hiệu đã tồn tại"); return; }

        Brand b = new Brand();
        b.setBrandId(UUID.randomUUID().toString());
        apply(b, body);
        brandService.themThuongHieu(b);
        ApiUtil.json(resp, 201, toMap(b));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        Brand b = id == null ? null : brandService.timThuongHieu(id);
        if (b == null) { ApiUtil.error(resp, 404, "Không tìm thấy thương hiệu"); return; }

        JsonObject body = ApiUtil.body(req);
        String name = ApiUtil.str(body, "name");
        if (name == null || name.length() < 2) { ApiUtil.error(resp, 400, "Nhập tên thương hiệu"); return; }
        if (nameExists(name, id)) { ApiUtil.error(resp, 409, "Thương hiệu đã tồn tại"); return; }

        apply(b, body);
        brandService.suaThuongHieu(b);
        ApiUtil.ok(resp, toMap(b));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || brandService.timThuongHieu(id) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy thương hiệu"); return;
        }
        int n = countProducts(id);
        if (n > 0) { ApiUtil.error(resp, 409, "Không thể xóa: còn " + n + " sản phẩm của thương hiệu này"); return; }

        brandService.xoaThuongHieu(id);
        ApiUtil.ok(resp, Map.of("ok", true));
    }
}
