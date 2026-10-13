package controller.User;

import controller.service.impl.ProductServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/products/*")
public class ProductServlet extends jakarta.servlet.http.HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();

    protected void send(jakarta.servlet.http.HttpServletResponse response, int status, Object body)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSON.toJson(body, response.getWriter());
    }

    protected model.entity.User currentUser(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("user");
        return value instanceof model.entity.User ? (model.entity.User) value : null;
    }

    protected String required(jakarta.servlet.http.HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    protected void unauthorized(jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        send(response, jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                new ApiMessage("Please log in"));
    }

    protected static final class ApiMessage {
        public final String message;
        ApiMessage(String message) { this.message = message; }
    }

    private final ProductServiceImpl service = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        String categoryId = request.getParameter("categoryId");
        String brandId = request.getParameter("brandId");
        String keyword = request.getParameter("q");

        if (id != null) {
            Product product = service.timSanPham(id);
            if (product == null) {
                send(response, HttpServletResponse.SC_NOT_FOUND,
                        new ApiMessage("Không tìm thấy sản phẩm"));
                return;
            }
            send(response, HttpServletResponse.SC_OK, toMap(product));
            return;
        }

        List<Product> products;
        if (categoryId != null) {
            products = service.getSanPhamTheoDanhMuc(categoryId);
        } else if (brandId != null) {
            products = service.getSanPhamTheoThuongHieu(brandId);
        } else if (keyword != null) {
            products = service.locSanPham(keyword);
        } else {
            products = service.getTatCaSanPham();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : products) {
            result.add(toMap(product));
        }
        send(response, HttpServletResponse.SC_OK, result);
    }

    private Map<String, Object> toMap(Product product) {
        Map<String, Object> result = new LinkedHashMap<>();
        String description = product.getDescription() == null ? "" : product.getDescription();
        String categoryName = product.getCategory() == null ? "" : product.getCategory().getCategoryName();
        String categoryId = product.getCategory() == null ? "" : product.getCategory().getCategoryId();
        String image = product.getImage() == null ? "" : product.getImage();
        result.put("id", product.getProductId());
        result.put("productId", product.getProductId());
        result.put("name", product.getProductName());
        result.put("productName", product.getProductName());
        result.put("description", description);
        result.put("desc", description);
        result.put("short", description);
        result.put("categoryId", categoryId);
        result.put("categoryName", categoryName);
        result.put("brandName", product.getBrand() == null ? "" : product.getBrand().getBrandName());
        result.put("price", product.getPrice());
        result.put("quantity", product.getQuantity());
        result.put("stock", product.getQuantity());
        result.put("image", image);
        result.put("img", image);
        result.put("old", null);
        result.put("rating", 0);
        result.put("reviews", 0);
        result.put("badge", "");
        result.put("isNew", false);
        return result;
    }
}
