package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.UserService;
import controller.util.ApiUtil;
import model.Role;
import model.entity.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/admin/api/staff")
public class AdminStaffServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> admins = userService.getUserTheoRole(Role.ADMIN).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        ApiUtil.ok(resp, admins);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = ApiUtil.body(req);

        String name = ApiUtil.str(body, "name");
        String email = ApiUtil.str(body, "email");
        String phone = ApiUtil.str(body, "phone");
        String password = ApiUtil.str(body, "password");

        if (name == null || name.length() < 2
                || email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || phone == null || phone.isBlank()
                || password == null || password.length() < 6) {
            ApiUtil.error(resp, 400, "Nhập họ tên, email, số điện thoại và mật khẩu hợp lệ (tối thiểu 6 ký tự)");
            return;
        }

        if (userService.getDanhSachUser().stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail()))) {
            ApiUtil.error(resp, 409, "Email đã tồn tại");
            return;
        }

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setFullName(name);
        user.setEmail(email.toLowerCase());
        user.setPhone(phone);
        user.setPassword(password);
        user.setRole(Role.ADMIN);

        userService.dangKy(user);
        ApiUtil.json(resp, 201, toMap(user));
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getUserId());
        map.put("name", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("role", user.getRole() == null ? null : user.getRole().name());
        return map;
    }
}