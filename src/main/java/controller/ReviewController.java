package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.ProductServiceImpl;
import controller.service.impl.ReviewServiceImpl;
import controller.service.intf.ProductService;
import controller.service.intf.ReviewService;
import model.entity.Product;
import model.entity.Review;
import model.entity.User;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {
    private final ReviewService reviewService = new ReviewServiceImpl();
    private final ProductService productService = new ProductServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String productId = value(req.getParameter("productId"));
        if (!productId.isEmpty()) {
            Product product = productService.timSanPham(productId);
            if (product == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("product", product);
            req.setAttribute("reviews", reviewService.getDanhGiaTheoSanPham(productId));
        }
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/product-detail.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String action = value(req.getParameter("action"));
        String productId = value(req.getParameter("productId"));
        try {
            if ("delete".equals(action) || "edit".equals(action)) {
                String reviewId = value(req.getParameter("reviewId"));
                Review review = reviewService.getDanhGiaTheoUser(user.getUserId()).stream()
                        .filter(r -> reviewId.equals(r.getReviewId())).findFirst().orElse(null);
                if (review == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (review.getProduct() != null) productId = review.getProduct().getProductId();
                if ("delete".equals(action)) reviewService.xoaDanhGia(reviewId);
                else {
                    int rating = Integer.parseInt(value(req.getParameter("rating")));
                    if (rating < 1 || rating > 5) throw new IllegalArgumentException();
                    review.updateReview(rating, value(req.getParameter("comment")));
                    reviewService.suaDanhGia(review);
                }
            } else if ("submit".equals(action) || action.isEmpty()) {
                Product product = productService.timSanPham(productId);
                int rating = Integer.parseInt(value(req.getParameter("rating")));
                String comment = value(req.getParameter("comment"));
                if (product == null || rating < 1 || rating > 5 || comment.isEmpty()) throw new IllegalArgumentException();
                Review review = new Review();
                review.setReviewId(UUID.randomUUID().toString());
                review.setProduct(product);
                review.setUser(user);
                review.setRating(rating);
                review.setComment(comment);
                reviewService.guiDanhGia(review);
            } else { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
            resp.sendRedirect(req.getContextPath() + "/product-detail?id=" + java.net.URLEncoder.encode(productId, java.nio.charset.StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nội dung đánh giá chưa hợp lệ");
        }
    }

    private static User currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User) s.getAttribute("user"); }
    private static String value(String s) { return s == null ? "" : s.trim(); }
}
