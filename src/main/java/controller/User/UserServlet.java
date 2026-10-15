package controller.User;

import controller.service.impl.AccountEmailNotificationService;
import controller.service.impl.OrderServiceImpl;
import controller.service.impl.UserServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Order;
import model.entity.Deposit;
import model.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/user/*")
public class UserServlet extends jakarta.servlet.http.HttpServlet {
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

    private final UserServiceImpl service = new UserServiceImpl();
    private final OrderServiceImpl orders = new OrderServiceImpl();
    private final AccountEmailNotificationService accountEmailService = new AccountEmailNotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        User user = currentUser(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        String path = request.getPathInfo();
        if ("/profile".equals(path)) {
            send(response, HttpServletResponse.SC_OK, profile(user));
            return;
        }
        if ("/orders".equals(path)) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Order order : orders.getDonHangTheoUser(user.getUserId())) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("orderId", order.getOrderId());
                row.put("orderDate", order.getOrderDate() == null ? null : order.getOrderDate().toString());
                row.put("status", order.getOrderStatus());
                row.put("totalAmount", order.getTotalAmount());
                Deposit deposit = order.getDeposit();
                java.math.BigDecimal depositAmount = deposit == null || deposit.getAmount() == null
                        ? java.math.BigDecimal.ZERO : deposit.getAmount();
                row.put("depositAmount", depositAmount);
                row.put("depositStatus", deposit == null || deposit.getDepositStatus() == null
                        ? null : deposit.getDepositStatus().name());
                row.put("remainingAmount", order.getTotalAmount().subtract(depositAmount).max(java.math.BigDecimal.ZERO));
                result.add(row);
            }
            send(response, HttpServletResponse.SC_OK, result);
            return;
        }
        send(response, HttpServletResponse.SC_NOT_FOUND,
                new ApiMessage("Không tìm thấy chức năng"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();
        if ("/register".equals(path)) {
            register(request, response);
            return;
        }
        if ("/login".equals(path)) {
            login(request, response);
            return;
        }

        User user = currentUser(request);
        if (user == null) {
            unauthorized(response);
            return;
        }
        if ("/logout".equals(path)) {
            request.getSession(false).invalidate();
            send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã đăng xuất"));
            return;
        }
        if ("/profile".equals(path)) {
            updateProfile(request, response, user);
            return;
        }
        if ("/password".equals(path)) {
            changePassword(request, response, user);
            return;
        }
        send(response, HttpServletResponse.SC_NOT_FOUND,
                new ApiMessage("Không tìm thấy chức năng"));
    }

    private void register(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = required(request, "fullName");
        String email = required(request, "email");
        String password = required(request, "password");
        String phone = required(request, "phone");
        if (name == null || email == null || password == null || phone == null) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Thiếu thông tin đăng ký"));
            return;
        }

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(request.getParameter("address"));
        try {
            if (!service.dangKy(user)) {
                send(response, HttpServletResponse.SC_CONFLICT,
                        new ApiMessage("Không thể tạo tài khoản"));
                return;
            }
            send(response, HttpServletResponse.SC_CREATED,
                    new ApiMessage("Đăng ký thành công"));
        } catch (RuntimeException exception) {
            send(response, HttpServletResponse.SC_CONFLICT,
                    new ApiMessage("Email đã được sử dụng hoặc dữ liệu không hợp lệ"));
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = service.dangNhap(
                required(request, "email"), required(request, "password"));
        if (user == null) {
            send(response, HttpServletResponse.SC_UNAUTHORIZED,
                    new ApiMessage("Thông tin đăng nhập không đúng"));
            return;
        }
        request.getSession(true).setAttribute("user", user);
        accountEmailService.sendLoginNotification(user);
        send(response, HttpServletResponse.SC_OK, profile(user));
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response,
                               User user) throws IOException {
        String name = required(request, "fullName");
        String email = required(request, "email");
        String phone = required(request, "phone");
        if (name == null || email == null || phone == null) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Thiếu thông tin hồ sơ"));
            return;
        }

        user.setFullName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(request.getParameter("address"));
        if (!service.suaThongTin(user)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiMessage("Không thể cập nhật hồ sơ"));
            return;
        }
        request.getSession().setAttribute("user", user);
        send(response, HttpServletResponse.SC_OK, profile(user));
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response,
                               User user) throws IOException {
        String oldPassword = required(request, "oldPassword");
        String newPassword = required(request, "newPassword");
        if (oldPassword == null || newPassword == null
                || !oldPassword.equals(user.getPassword()) || newPassword.length() < 8) {
            send(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Mật khẩu hiện tại không đúng hoặc mật khẩu mới chưa hợp lệ"));
            return;
        }

        service.doiMatKhau(user.getUserId(), newPassword);
        user.setPassword(newPassword);
        request.getSession().setAttribute("user", user);
        send(response, HttpServletResponse.SC_OK,
                new ApiMessage("Đổi mật khẩu thành công"));
    }

    private Map<String, Object> profile(User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.getUserId());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("address", user.getAddress());
        return result;
    }
}

