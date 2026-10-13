package controller.User;

import controller.service.impl.UserFeatureServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.User;
import model.entity.WishlistItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/wishlist/*")
public class WishlistServlet extends jakarta.servlet.http.HttpServlet {
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

    private final UserFeatureServiceImpl service = new UserFeatureServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        List<Map<String, Object>> result = new ArrayList<>();
        for (WishlistItem item : service.getWishlist(user.getUserId())) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productId", item.getProduct().getProductId());
            row.put("productName", item.getProduct().getProductName());
            row.put("price", item.getProduct().getPrice());
            row.put("image", item.getProduct().getImage());
            result.add(row);
        }
        send(response, HttpServletResponse.SC_OK, result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String productId = required(request, "productId");
        if (productId == null || !service.addWishlistItem(user.getUserId(), productId)) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Không thể thêm sản phẩm yêu thích"));
            return;
        }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã thêm vào yêu thích"));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String productId = required(request, "productId");
        if (productId == null || !service.removeWishlistItem(user.getUserId(), productId)) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Không thể xóa sản phẩm yêu thích"));
            return;
        }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã xóa khỏi yêu thích"));
    }
}
