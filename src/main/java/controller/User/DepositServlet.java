package controller.User;

import controller.service.impl.DepositServiceImpl;
import controller.service.impl.OrderServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Deposit;
import model.entity.Order;
import model.entity.User;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/deposits/*")
public class DepositServlet extends jakarta.servlet.http.HttpServlet {
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

    private final DepositServiceImpl service = new DepositServiceImpl();
    private final OrderServiceImpl orders = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = currentUser(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        String orderId = required(request, "orderId");
        if (orderId == null) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Thiếu orderId"));
            return;
        }

        boolean owned = false;
        for (Order order : orders.getDonHangTheoUser(user.getUserId())) {
            if (order.getOrderId().equals(orderId)) {
                owned = true;
                break;
            }
        }
        Deposit deposit = owned ? service.getDatCocTheoDonHang(orderId) : null;
        if (deposit == null) {
            send(response, HttpServletResponse.SC_NOT_FOUND,
                    new ApiMessage("Không tìm thấy đặt cọc"));
            return;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("depositId", deposit.getDepositId());
        result.put("amount", deposit.getAmount());
        result.put("status", deposit.getDepositStatus());
        result.put("depositDate", deposit.getDepositDate());
        send(response, HttpServletResponse.SC_OK, result);
    }
}
