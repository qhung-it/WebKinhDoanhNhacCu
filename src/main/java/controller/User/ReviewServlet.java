package controller.User;

import controller.service.impl.ReviewServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.impl.ProductDAOImpl;
import model.entity.Product;
import model.entity.Review;
import model.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/reviews/*")
public class ReviewServlet extends jakarta.servlet.http.HttpServlet {
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

    private final ReviewServiceImpl service = new ReviewServiceImpl();
    private final ProductDAOImpl products = new ProductDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String productId = required(request, "productId");
        if (productId == null) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Thiếu productId"));
            return;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review review : service.getDanhGiaTheoSanPham(productId)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("reviewId", review.getReviewId());
            row.put("rating", review.getRating());
            row.put("comment", review.getComment());
            row.put("name", review.getUser().getFullName());
            row.put("text", review.getComment());
            row.put("verified", false);
            result.add(row);
        }
        send(response, HttpServletResponse.SC_OK, result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = currentUser(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        String productId = required(request, "productId");
        String comment = required(request, "comment");
        int rating;
        try {
            rating = Integer.parseInt(required(request, "rating"));
        } catch (Exception exception) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Rating phải từ 1 đến 5"));
            return;
        }
        if (productId == null || rating < 1 || rating > 5) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Thông tin đánh giá không hợp lệ"));
            return;
        }

        Product product = products.findById(productId);
        if (product == null) {
            send(response, HttpServletResponse.SC_NOT_FOUND,
                    new ApiMessage("Không tìm thấy sản phẩm"));
            return;
        }

        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        service.guiDanhGia(review);
        send(response, HttpServletResponse.SC_CREATED,
                new ApiMessage("Đã gửi đánh giá"));
    }
}
