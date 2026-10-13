package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.ReviewServiceImpl;
import controller.service.intf.ReviewService;
import controller.util.ApiUtil;
import model.entity.Review;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/admin/api/reviews/*")
public class AdminReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewServiceImpl();

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
        List<Map<String, Object>> reviews = reviewService.getDanhSachDanhGia().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        ApiUtil.ok(resp, reviews);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);

        if (id == null) {
            ApiUtil.error(resp, 400, "Thiếu mã đánh giá");
            return;
        }

        JsonObject body = ApiUtil.body(req);

        if (!body.has("approved") || body.get("approved").isJsonNull()) {
            ApiUtil.error(resp, 400, "Thiếu trạng thái duyệt");
            return;
        }

        if (reviewService.getDanhSachDanhGia().stream().noneMatch(r -> id.equals(r.getReviewId()))) {
            ApiUtil.error(resp, 404, "Không tìm thấy đánh giá");
            return;
        }

        reviewService.capNhatDuyetDanhGia(id, body.get("approved").getAsBoolean());
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || reviewService.getDanhSachDanhGia().stream().noneMatch(r -> id.equals(r.getReviewId()))) {
            ApiUtil.error(resp, 404, "Không tìm thấy đánh giá");
            return;
        }
        reviewService.xoaDanhGia(id);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    private String idFromPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        return (path == null || path.length() < 2) ? null : path.substring(1).split("/")[0];
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", review.getReviewId());
        map.put("productId", review.getProduct() == null ? null : review.getProduct().getProductId());
        map.put("productName", review.getProduct() == null ? "" : review.getProduct().getProductName());
        map.put("userId", review.getUser() == null ? null : review.getUser().getUserId());
        map.put("userName", review.getUser() == null ? "Khách" : review.getUser().getFullName());
        map.put("rating", review.getRating());
        map.put("comment", review.getComment());
        map.put("verified", review.getOrder() != null);
        map.put("approved", review.isApproved());
        return map;
    }
}