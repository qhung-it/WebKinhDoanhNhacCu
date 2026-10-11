package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import controller.service.impl.OrderServiceImpl;
import controller.service.impl.ProductServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.intf.OrderService;
import controller.service.intf.ProductService;
import controller.service.intf.PaymentService;
import model.OrderStatus;
import model.PaymentMethod;
import model.entity.Order;
import model.entity.OrderLineItem;
import model.entity.Payment;
import model.entity.Product;
import model.entity.User;
import java.math.BigDecimal;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/orders")
public class OrderController extends HttpServlet {
    private final OrderService orderService = new OrderServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login?returnUrl=orders"); return; }
        req.setAttribute("orders", orderService.getDonHangTheoUser(user.getUserId()));
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login?returnUrl=checkout"); return; }
        String action = value(req.getParameter("action"));
        if ("cancel".equals(action)) {
            String id = value(req.getParameter("orderId"));
            Order order = orderService.getDonHangTheoUser(user.getUserId()).stream()
                    .filter(o -> id.equals(o.getOrderId())).findFirst().orElse(null);
            if (order == null || order.getOrderStatus() != OrderStatus.PENDING) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND); return;
            }
            orderService.huyDonHang(id);
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }
        if (!action.isEmpty() && !"create".equals(action)) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
        String[] productIds = req.getParameterValues("productId");
        String[] quantities = req.getParameterValues("quantity");
        if (productIds == null || quantities == null || productIds.length == 0 || productIds.length != quantities.length) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Giỏ hàng không hợp lệ"); return;
        }
        try {
            Order order = new Order();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setUser(user);
            order.setShippingAddress(value(req.getParameter("address")));
            if (order.getShippingAddress().isEmpty()) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu địa chỉ nhận hàng"); return; }
            for (int i = 0; i < productIds.length; i++) {
                Product product = productService.timSanPham(value(productIds[i]));
                int quantity = Integer.parseInt(quantities[i]);
                if (product == null || quantity <= 0 || !product.checkStock(quantity)) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sản phẩm không tồn tại hoặc không đủ hàng"); return; }
                OrderLineItem line = new OrderLineItem();
                line.setOrderLineItemId(UUID.randomUUID().toString());
                line.setProduct(product);
                line.setQuantity(quantity);
                order.addLineItem(line);
            }
            // Discount rules depend on lazy promotion relations that the current service API does not load.
            order.setDiscount(BigDecimal.ZERO);
            orderService.taoDonHang(order);
            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod("ewallet".equalsIgnoreCase(value(req.getParameter("paymentMethod"))) ? PaymentMethod.E_WALLET : PaymentMethod.CASH);
            paymentService.taoThanhToan(payment);
            req.getSession().setAttribute("orderMessage", "Đã tiếp nhận đơn hàng " + order.getOrderId());
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (IllegalArgumentException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Số lượng sản phẩm không hợp lệ");
        }
    }

    private static User currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User) s.getAttribute("user"); }
    private static String value(String s) { return s == null ? "" : s.trim(); }
}
