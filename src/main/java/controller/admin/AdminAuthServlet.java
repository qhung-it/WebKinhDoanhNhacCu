package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.UserService;
import controller.util.ApiUtil;
import model.Role;
import model.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/admin/api/auth/*")
public class AdminAuthServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    /**
     * admin.js gọi khi tải trang để biết đã đăng nhập chưa
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        Object u = (s == null) ? null : s.getAttribute("adminUser");

        if (u == null) {
            ApiUtil.error(resp, 401, "Chưa đăng nhập");
            return;
        }

        ApiUtil.ok(resp, u);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();

        if (path.equals("/login")) {
            JsonObject b = ApiUtil.body(req);
            String email = ApiUtil.str(b, "email");
            String pass = ApiUtil.str(b, "password");

            if (email == null || pass == null) {
                ApiUtil.error(resp, 400, "Nhập email và mật khẩu");
                return;
            }

            User u = userService.dangNhap(email, pass);

            // Cùng một thông báo cho "sai mật khẩu" và "không phải admin" để không lộ thông tin
            if (u == null || u.getRole() != Role.ADMIN) {
                ApiUtil.error(resp, 401, "Sai email hoặc mật khẩu");
                return;
            }

            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate(); // chống session fixation
            }

            HttpSession s = req.getSession(true);
            Map<String, Object> info = toSession(u);
            s.setAttribute("adminUser", info);
            ApiUtil.ok(resp, info);

        } else if (path.equals("/logout")) {
            HttpSession s = req.getSession(false);
            if (s != null) {
                s.invalidate();
            }
            ApiUtil.ok(resp, Map.of("ok", true));

        } else {
            ApiUtil.error(resp, 404, "Không tìm thấy");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUser") == null) {
            ApiUtil.error(resp, 401, "Chưa đăng nhập");
            return;
        }

        JsonObject body = ApiUtil.body(req);
        String current = ApiUtil.str(body, "currentPassword");
        String next = ApiUtil.str(body, "newPassword");

        @SuppressWarnings("unchecked")
        Map<String, Object> admin = (Map<String, Object>) session.getAttribute("adminUser");
        User user = userService.dangNhap(String.valueOf(admin.get("email")), current);

        if (user == null) {
            ApiUtil.error(resp, 400, "Mật khẩu hiện tại không chính xác");
            return;
        }

        if (next == null || next.length() < 6) {
            ApiUtil.error(resp, 400, "Mật khẩu mới tối thiểu 6 ký tự");
            return;
        }

        userService.doiMatKhau(user.getUserId(), next);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    private Map<String, Object> toSession(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getUserId());
        m.put("name", u.getFullName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole().name().toLowerCase()); // "admin" khớp với ROLES.admin trong admin.js
        return m;
    }
}