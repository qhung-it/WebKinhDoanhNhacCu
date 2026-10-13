package controller.admin;

import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.intf.OrderService;
import controller.service.intf.PaymentService;
import controller.util.ApiUtil;
import model.PaymentStatus;
import model.entity.Order;
import model.entity.Payment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/api/payments/*")
public class AdminPaymentServlet extends HttpServlet {

    private final OrderService orderService = new OrderServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> payments = new ArrayList<>();
        for (Order order : orderService.getDanhSachDonHang()) {
            Payment payment = paymentService.getThanhToanTheoDonHang(order.getOrderId());
            if (payment != null) {
                payments.add(toMap(order, payment));
            }
        }
        ApiUtil.ok(resp, payments);
    }

    /** PATCH /admin/api/payments/{orderId}/status body: {"status":"COMPLETED|REFUNDED"} */
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderId = idFromPath(req);
        if (orderId == null) {
            ApiUtil.error(resp, 400, "Thiếu mã đơn hàng");
            return;
        }

        Payment payment = paymentService.getThanhToanTheoDonHang(orderId);
        if (payment == null) {
            ApiUtil.error(resp, 404, "Đơn hàng chưa có giao dịch thanh toán");
            return;
        }

        String rawStatus = ApiUtil.str(ApiUtil.body(req), "status");
        PaymentStatus status;
        try {
            status = PaymentStatus.valueOf(rawStatus == null ? "" : rawStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, "Trạng thái thanh toán không hợp lệ");
            return;
        }

        if (status == PaymentStatus.PENDING || status == PaymentStatus.FAILED) {
            ApiUtil.error(resp, 400, "Chỉ chấp nhận xác nhận đã thanh toán hoặc hoàn tiền");
            return;
        }

        if (status == PaymentStatus.REFUNDED) {
            if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                ApiUtil.error(resp, 409, "Chỉ hoàn tiền cho giao dịch đã thanh toán");
                return;
            }
            paymentService.hoanTien(payment.getPaymentId());
        } else {
            paymentService.xuLyThanhToan(payment.getPaymentId());
        }

        ApiUtil.ok(resp, toMap(orderService.timDonHang(orderId), paymentService.getThanhToanTheoDonHang(orderId)));
    }

    private String idFromPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            return null;
        }
        return path.substring(1).split("/")[0];
    }

    private Map<String, Object> toMap(Order order, Payment payment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", payment == null ? null : payment.getPaymentId());
        map.put("orderId", order.getOrderId());
        map.put("orderStatus", order.getOrderStatus() == null ? null : order.getOrderStatus().name());
        map.put("method", (payment == null || payment.getPaymentMethod() == null)
                ? null : payment.getPaymentMethod().name());
        map.put("amount", payment == null ? order.getTotalAmount() : payment.getAmount());
        map.put("status", (payment == null || payment.getPaymentStatus() == null)
                ? PaymentStatus.PENDING.name() : payment.getPaymentStatus().name());
        map.put("paidAt", payment == null ? null : payment.getPaymentDate());
        map.put("customerName", order.getUser() == null ? "Khách lẻ" : order.getUser().getFullName());
        return map;
    }
}