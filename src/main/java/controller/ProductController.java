package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.ProductServiceImpl;
import controller.service.impl.PromotionServiceImpl;
import controller.service.impl.ReviewServiceImpl;
import controller.service.intf.ProductService;
import controller.service.intf.PromotionService;
import controller.service.intf.ReviewService;
import model.entity.Product;
import model.entity.Brand;
import model.entity.Category;
import model.entity.Promotion;
import model.entity.Review;
import model.DiscountType;
import java.io.IOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import java.text.Normalizer;

@WebServlet(urlPatterns = {"/products", "/product-detail"})
public class ProductController extends HttpServlet {
    private final ProductService productService = new ProductServiceImpl();
    private final PromotionService promotionService = new PromotionServiceImpl();
    private final ReviewService reviewService = new ReviewServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        String id = trim(req.getParameter("id"));
        if ("/product-detail".equals(path)) {
            if (id.isEmpty()) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã sản phẩm"); return; }
            Product product = productService.timSanPham(id);
            if (product == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("product", product);
            req.setAttribute("productId", id);
            req.setAttribute("promotions", promotionService.getKhuyenMaiTheoSanPham(id));
            req.setAttribute("reviews", reviewService.getDanhGiaTheoSanPham(id));
            preparePageData(req);
            req.getRequestDispatcher("/product-detail.jsp").forward(req, resp);
            return;
        }

        String categoryId = trim(req.getParameter("categoryId"));
        String brandId = trim(req.getParameter("brandId"));
        String keyword = trim(req.getParameter("keyword"));
        if (!categoryId.isEmpty()) req.setAttribute("products", productService.getSanPhamTheoDanhMuc(categoryId));
        else if (!brandId.isEmpty()) req.setAttribute("products", productService.getSanPhamTheoThuongHieu(brandId));
        else req.setAttribute("products", productService.locSanPham(keyword));
        req.setAttribute("keyword", keyword);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("brandId", brandId);
        preparePageData(req);
        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private static String trim(String value) { return value == null ? "" : value.trim(); }

    public static void preparePageData(HttpServletRequest req) {
        ProductService productsService = new ProductServiceImpl();
        PromotionService promotionsService = new PromotionServiceImpl();
        ReviewService reviewsService = new ReviewServiceImpl();
        List<Product> rows = productsService.locSanPham("");
        List<Product> products = new java.util.ArrayList<>();
        Map<String, Category> categories = new LinkedHashMap<>();
        Map<String, Brand> brands = new LinkedHashMap<>();
        for (Product row : rows) {
            Product p = productsService.timSanPham(row.getProductId());
            if (p == null) continue;
            products.add(p);
            if (p.getCategory() != null && p.getCategory().isStatus())
                categories.putIfAbsent(p.getCategory().getCategoryId(), p.getCategory());
            if (p.getBrand() != null)
                brands.putIfAbsent(p.getBrand().getBrandId(), p.getBrand());
        }
        StringBuilder data = new StringBuilder("{\"products\":[");
        boolean first = true;
        for (Product p : products) {
            String categoryName = p.getCategory() == null ? "Khác" : p.getCategory().getCategoryName();
            String categoryKey = slug(categoryName);
            String brandName = p.getBrand() == null ? "Khác" : p.getBrand().getBrandName();
            List<Review> reviews = reviewsService.getDanhGiaTheoSanPham(p.getProductId());
            double rating = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            if (!first) data.append(',');
            first = false;
            data.append("{\"id\":").append(json(p.getProductId()))
                .append(",\"name\":").append(json(p.getProductName()))
                .append(",\"cat\":").append(json(categoryKey))
                .append(",\"catName\":").append(json(categoryName))
                .append(",\"emoji\":").append(json(categoryIcon(categoryKey)))
                .append(",\"brand\":").append(json(brandName))
                .append(",\"price\":").append(p.getPrice() == null ? "0" : p.getPrice().toPlainString())
                .append(",\"old\":").append(oldPrice(p, promotionsService))
                .append(",\"rating\":").append(rating).append(",\"reviews\":").append(reviews.size())
                .append(",\"badge\":null,\"sold\":0,\"isNew\":false")
                .append(",\"sku\":").append(json(p.getProductId()))
                .append(",\"stock\":").append(p.getQuantity())
                .append(",\"short\":").append(json(p.getDescription()))
                .append(",\"desc\":").append(json(p.getDescription()))
                .append(",\"features\":[").append(json(p.getDescription())).append(']')
                .append(",\"specs\":[[\"Thương hiệu\",").append(json(brandName))
                .append("],[\"Danh mục\",").append(json(categoryName)).append("]]")
                .append(",\"image\":").append(json(p.getImage())).append('}');
        }
        data.append("],\"categories\":[");
        first=true;
        for (Category c:categories.values()) {
            if (!first) data.append(',');
            first=false;
            String key=slug(c.getCategoryName());
            data.append("{\"key\":").append(json(key)).append(",\"id\":").append(json(c.getCategoryId()))
                .append(",\"name\":").append(json(c.getCategoryName()))
                .append(",\"icon\":").append(json(categoryIcon(key))).append('}');
        }
        data.append("],\"brands\":[");
        first=true;
        for (Brand b:brands.values()) {
            if (!first) data.append(',');
            first=false;
            data.append("{\"name\":").append(json(b.getBrandName()))
                .append(",\"origin\":\"\",\"desc\":").append(json(b.getDescription())).append('}');
        }
        data.append("]}");
        req.setAttribute("initialDataJson", data.toString());
    }

    private static String slug(String value) {
        if (value == null) return "other";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private static String categoryIcon(String key) {
        if (key.contains("guitar")) return "🎸";
        if (key.contains("piano") || key.contains("organ")) return "🎹";
        if (key.contains("trong")) return "🥁";
        if (key.contains("ukulele")) return "🪕";
        if (key.contains("violin")) return "🎻";
        if (key.contains("ken") || key.contains("sao")) return "🎷";
        return "🎵";
    }

    private static String oldPrice(Product product, PromotionService promotionService) {
        if (product.getPrice() == null) return "null";
        for (Promotion promotion : promotionService.getKhuyenMaiTheoSanPham(product.getProductId())) {
            if (!promotion.isActive() || promotion.getDiscountValue() == null) continue;
            double value = promotion.getDiscountValue().doubleValue();
            if (value <= 0) continue;
            double old = promotion.getDiscountType() == DiscountType.PERCENTAGE && value < 100
                    ? product.getPrice().doubleValue() / (1 - value / 100)
                    : product.getPrice().doubleValue() + value;
            return java.math.BigDecimal.valueOf(old).setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();
        }
        return "null";
    }

    private static String json(String value) {
        if (value == null) return "null";
        StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                case '<': result.append("\\u003c"); break;
                case '>': result.append("\\u003e"); break;
                case '&': result.append("\\u0026"); break;
                default: if (c < 0x20) result.append(String.format("\\u%04x", (int) c)); else result.append(c);
            }
        }
        return result.append('"').toString();
    }
}
