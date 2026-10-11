package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.DepositServiceImpl;
import controller.service.impl.OrderServiceImpl;
import controller.service.intf.DepositService;
import controller.service.intf.OrderService;
import model.entity.Deposit;
import model.entity.Order;
import model.entity.User;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/deposit")
public class DepositController extends HttpServlet {
    private final DepositService depositService = new DepositServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String orderId = value(req.getParameter("orderId"));
        Order order = ownedOrder(orderId, user);
        if (order == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        req.setAttribute("order", order);
        req.setAttribute("deposit", depositService.getDatCocTheoDonHang(orderId));
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String orderId = value(req.getParameter("orderId"));
        Order order = ownedOrder(orderId, user);
        if (order == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        if (depositService.getDatCocTheoDonHang(orderId) != null) { resp.sendError(HttpServletResponse.SC_CONFLICT, "Đơn hàng đã có đặt cọc"); return; }
        Deposit deposit = new Deposit();
        deposit.setDepositId(UUID.randomUUID().toString());
        deposit.setOrder(order);
        deposit.setAmount(deposit.calculateDeposit());
        if (deposit.getAmount().signum() <= 0) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Đơn hàng không thuộc diện đặt cọc"); return; }
        depositService.taoDatCoc(deposit);
        resp.sendRedirect(req.getContextPath() + "/orders");
    }

    private Order ownedOrder(String id, User user) {
        return orderService.getDonHangTheoUser(user.getUserId()).stream()
                .filter(order -> id.equals(order.getOrderId())).findFirst().orElse(null);
    }
    private static User currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User) s.getAttribute("user"); }
    private static String value(String s) { return s == null ? "" : s.trim(); }
}
