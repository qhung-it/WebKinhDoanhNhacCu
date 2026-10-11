package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.UserService;
import model.entity.User;
import java.io.IOException;
import java.util.UUID;

@WebServlet(urlPatterns = {"/login", "/logout", "/account"})
public class UserController extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = currentUser(req);
        if ("/account".equals(path)) {
            if (user == null) { resp.sendRedirect(req.getContextPath() + "/login?returnUrl=account"); return; }
            req.setAttribute("user", user);
            req.setAttribute("orders", userService.getLichSuMuaHang(user.getUserId()));
        }
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = value(req.getParameter("action"));
        try {
            if ("login".equals(action)) {
                User user = userService.dangNhap(value(req.getParameter("email")), value(req.getParameter("password")));
                if (user == null) { req.getSession().setAttribute("userMessage", "Email hoặc mật khẩu không đúng."); redirect(req, resp, "/login"); return; }
                req.getSession().setAttribute("user", user);
                String next = value(req.getParameter("returnUrl"));
                redirect(req, resp, next.startsWith("/") || next.contains(":") ? "/" : "/" + (next.isEmpty() ? "account" : next));
            } else if ("register".equals(action)) {
                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setFullName(value(req.getParameter("fullName")));
                user.setEmail(value(req.getParameter("email")));
                user.setPhone(value(req.getParameter("phone")));
                user.setAddress(value(req.getParameter("address")));
                user.setPassword(value(req.getParameter("password")));
                if (user.getFullName().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty() || user.getPassword().length() < 6) throw new IllegalArgumentException();
                userService.dangKy(user);
                req.getSession().setAttribute("user", user);
                redirect(req, resp, "/account");
            } else if ("profile".equals(action)) {
                User current = requireUser(req, resp); if (current == null) return;
                current.setFullName(value(req.getParameter("fullName")));
                current.setEmail(value(req.getParameter("email")));
                current.setPhone(value(req.getParameter("phone")));
                current.setAddress(value(req.getParameter("address")));
                if (current.getFullName().isEmpty() || current.getEmail().isEmpty() || current.getPhone().isEmpty()) throw new IllegalArgumentException();
                userService.suaThongTin(current);
                req.getSession().setAttribute("user", current);
                req.getSession().setAttribute("userMessage", "Đã cập nhật thông tin.");
                redirect(req, resp, "/account");
            } else if ("password".equals(action)) {
                User current = requireUser(req, resp); if (current == null) return;
                String oldPassword = value(req.getParameter("currentPassword"));
                String newPassword = value(req.getParameter("newPassword"));
                if (!oldPassword.equals(current.getPassword()) || newPassword.length() < 6) throw new IllegalArgumentException();
                userService.doiMatKhau(current.getUserId(), newPassword);
                current.setPassword(newPassword);
                req.getSession().setAttribute("user", current);
                req.getSession().setAttribute("userMessage", "Đã đổi mật khẩu.");
                redirect(req, resp, "/account");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("userMessage", "Thông tin gửi lên chưa hợp lệ.");
            redirect(req, resp, "/login");
        }
    }

    private User requireUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = currentUser(req);
        if (user == null) redirect(req, resp, "/login");
        return user;
    }
    private static User currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User) s.getAttribute("user"); }
    private static String value(String s) { return s == null ? "" : s.trim(); }
    private static void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException { resp.sendRedirect(req.getContextPath() + path); }
}
