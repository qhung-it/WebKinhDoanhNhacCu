package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import controller.service.impl.CartServiceImpl;
import controller.service.intf.CartService;
import model.entity.User;
import jakarta.persistence.NoResultException;

@WebServlet(urlPatterns = {"/cart", "/checkout"})
public class CartController extends HttpServlet {
    private final CartService cartService = new CartServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user != null && user.getUserId() != null) {
            try { req.setAttribute("cart", cartService.getGioHangTheoUser(user.getUserId())); }
            catch (NoResultException ex) { req.setAttribute("cart", null); }
            req.setAttribute("user", user);
        }
        String view = req.getServletPath().equals("/checkout") ? "/checkout.jsp" : "/cart.jsp";
        ProductController.preparePageData(req);
        req.getRequestDispatcher(view).forward(req, resp);
    }
}
