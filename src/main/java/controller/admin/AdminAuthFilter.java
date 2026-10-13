package controller.admin;


import controller.util.ApiUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/api/*")
public class AdminAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rp;

        // đường dẫn không gồm context path, vd "/admin/api/auth/login"
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/admin/api/auth")) { chain.doFilter(rq, rp); return; }

        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("adminUser") == null) {
            ApiUtil.error(resp, 401, "Chưa đăng nhập");
            return;
        }
        chain.doFilter(rq, rp);
    }
}
