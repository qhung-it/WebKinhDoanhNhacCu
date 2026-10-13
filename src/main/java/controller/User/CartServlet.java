package controller.User;

import controller.service.impl.CartServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Cart;
import model.entity.CartLineItem;
import model.entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cart/*")
public class CartServlet extends jakarta.servlet.http.HttpServlet {
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

    private final CartServiceImpl service = new CartServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        Cart cart = service.getGioHangTheoUser(user.getUserId());
        if (cart == null) { send(response, HttpServletResponse.SC_OK, Collections.emptyMap()); return; }
        List<Map<String, Object>> items = new ArrayList<>();
        for (CartLineItem item : cart.getCartLineItems()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productId", item.getProduct().getProductId());
            row.put("quantity", item.getQuantity());
            row.put("productName", item.getProduct().getProductName());
            row.put("price", item.getProduct().getPrice());
            row.put("image", item.getProduct().getImage());
            items.add(row);
        }
        send(response, HttpServletResponse.SC_OK, items);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String productId = required(request, "productId");
        int quantity = parseQuantity(request.getParameter("quantity"));
        if (productId == null || quantity < 1 || !service.themSanPham(user.getUserId(), productId, quantity)) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Không thể thêm sản phẩm vào giỏ; kiểm tra tồn kho"));
            return;
        }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã thêm vào giỏ hàng"));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String productId = required(request, "productId");
        int quantity = parseQuantity(request.getParameter("quantity"));
        if (productId == null || quantity < 1 || !service.capNhatSanPham(user.getUserId(), productId, quantity)) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Không thể cập nhật số lượng"));
            return;
        }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã cập nhật giỏ hàng"));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String productId = required(request, "productId");
        boolean success = productId == null
                ? service.xoaTatCa(user.getUserId())
                : service.xoaSanPham(user.getUserId(), productId);
        if (!success) { send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Không thể xóa khỏi giỏ")); return; }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã cập nhật giỏ hàng"));
    }

    private int parseQuantity(String value) {
        try { return Integer.parseInt(value); } catch (RuntimeException e) { return -1; }
    }
}
