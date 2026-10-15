package controller.admin;

import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.intf.OrderService;
import controller.service.intf.PaymentService;
import controller.util.ApiUtil;
import model.OrderStatus;
import model.entity.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/api/orders/*")
public class AdminOrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();

    private String payLabel(Order order, Payment payment) {
        if (payment == null || payment.getPaymentStatus() == null) return "Ch\u01b0a thanh to\u00e1n";
        if (payment.getPaymentStatus() == model.PaymentStatus.COMPLETED
                && order.getDeposit() != null
                && payment.getAmount() != null
                && order.getTotalAmount() != null
                && payment.getAmount().compareTo(order.getTotalAmount()) < 0) {
            return "DEPOSITED";
        }
        switch (payment.getPaymentStatus()) {
            case COMPLETED: return "\u0110\u00e3 thanh to\u00e1n";
            case REFUNDED:  return "\u0110\u00e3 ho\u00e0n ti\u1ec1n";
            default:        return "Ch\u01b0a thanh to\u00e1n";
        }
    }

    private Map<String, Object> toMap(Order o) {
        User u = o.getUser();
        Payment pay = paymentService.getThanhToanTheoDonHang(o.getOrderId());

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getOrderId());
        m.put("customerName", u == null ? "Khách lẻ" : u.getFullName());
        m.put("customerPhone", u == null ? "" : u.getPhone());
        m.put("customerEmail", u == null ? "" : u.getEmail());
        m.put("address", o.getShippingAddress());
        m.put("note", o.getNote());
        m.put("payMethod", pay == null || pay.getPaymentMethod() == null
                ? null : pay.getPaymentMethod().name().toLowerCase());
        Deposit deposit = o.getDeposit();
        java.math.BigDecimal depositAmount = deposit == null || deposit.getAmount() == null
                ? java.math.BigDecimal.ZERO : deposit.getAmount();
        m.put("payStatus", payLabel(o, pay));
        m.put("depositAmount", depositAmount);
        m.put("remainingAmount", o.getTotalAmount() == null
                ? java.math.BigDecimal.ZERO : o.getTotalAmount().subtract(depositAmount).max(java.math.BigDecimal.ZERO));
        m.put("status", o.getOrderStatus() == null ? null : o.getOrderStatus().name());
        m.put("createdAt", o.getOrderDate());
        m.put("subtotal", o.getSubtotal());
        m.put("discount", o.getDiscount());
        m.put("couponCode", o.getCouponCode());
        m.put("shippingFee", o.getShippingFee());
        m.put("total", o.getTotalAmount());

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderLineItem it : o.getOrderLineItems()) {
            Product p = it.getProduct();
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("productId", p == null ? null : p.getProductId());
            x.put("name", p == null ? "" : p.getProductName());
            x.put("price", it.getUnitPrice() != null ? it.getUnitPrice() : (p == null ? null : p.getPrice()));
            x.put("qty", it.getQuantity());
            x.put("image", p == null ? null : p.getImage());
            items.add(x);
        }
        m.put("items", items);

        List<Map<String, Object>> hist = new ArrayList<>();
        for (OrderHistory h : o.getHistory()) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("status", h.getStatus().name());
            x.put("at", h.getChangedAt());
            x.put("by", h.getChangedBy());
            hist.add(x);
        }
        m.put("history", hist);
        return m;
    }

    private String idFromPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) return null;
        return p.substring(1).split("/")[0];
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ApiUtil.ok(resp, orderService.getDanhSachDonHang()
                .stream().map(this::toMap).collect(Collectors.toList()));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) { doPatch(req, resp); return; }
        super.service(req, resp);
    }

    /** PATCH /admin/api/orders/{id}/status   body: {"status":"CONFIRMED"} */
    @SuppressWarnings("unchecked")
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || orderService.timDonHang(id) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy đơn hàng"); return;
        }
        OrderStatus st;
        try {
            st = OrderStatus.valueOf(String.valueOf(ApiUtil.str(ApiUtil.body(req), "status")));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, "Trạng thái không hợp lệ"); return;
        }

        Map<String, Object> admin = (Map<String, Object>) req.getSession().getAttribute("adminUser");
        String by = admin == null ? "admin" : String.valueOf(admin.get("email"));
        try {
            orderService.capNhatTrangThai(id, st, by);
            ApiUtil.ok(resp, Map.of("ok", true));
        } catch (IllegalStateException e) {
            ApiUtil.error(resp, 409, e.getMessage());
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }
}
