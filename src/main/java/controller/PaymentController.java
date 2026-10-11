package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.intf.OrderService;
import controller.service.intf.PaymentService;
import model.entity.Order;
import model.entity.Payment;
import model.entity.User;
import java.io.IOException;

@WebServlet("/payment")
public class PaymentController extends HttpServlet {
    private final PaymentService paymentService = new PaymentServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String orderId = value(req.getParameter("orderId"));
        if (user != null && !orderId.isEmpty()) {
            Order order = ownedOrder(orderId, user);
            if (order == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("order", order);
            req.setAttribute("payment", paymentService.getThanhToanTheoDonHang(orderId));
        }
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String orderId = value(req.getParameter("orderId"));
        Order order = ownedOrder(orderId, user);
        if (order == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        Payment payment = paymentService.getThanhToanTheoDonHang(orderId);
        if (payment == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy thanh toán"); return;
        }
        paymentService.xuLyThanhToan(payment.getPaymentId());
        req.getSession().setAttribute("orderMessage", "Đã cập nhật trạng thái thanh toán.");
        resp.sendRedirect(req.getContextPath() + "/orders");
    }

    private Order ownedOrder(String id, User user) {
        return orderService.getDonHangTheoUser(user.getUserId()).stream()
                .filter(order -> id.equals(order.getOrderId())).findFirst().orElse(null);
    }
    private static User currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User) s.getAttribute("user"); }
    private static String value(String s) { return s == null ? "" : s.trim(); }
}
