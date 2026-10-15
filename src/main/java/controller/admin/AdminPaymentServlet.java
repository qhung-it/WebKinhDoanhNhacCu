package controller.admin;

import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.intf.OrderService;
import controller.service.intf.PaymentService;
import controller.util.ApiUtil;
import com.google.gson.JsonObject;
import model.PaymentStatus;
import model.OrderStatus;
import model.entity.Deposit;
import model.entity.Order;
import model.entity.Payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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

        JsonObject body = ApiUtil.body(req);
        String rawStatus = ApiUtil.str(body, "status");
        PaymentStatus status;
        try {
            status = PaymentStatus.valueOf(rawStatus == null ? "" : rawStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, "Invalid payment status");
            return;
        }
        if (status == PaymentStatus.PENDING || status == PaymentStatus.FAILED) {
            ApiUtil.error(resp, 400, "Only collection confirmation or refund is supported");
            return;
        }

        if (status == PaymentStatus.REFUNDED) {
            if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                ApiUtil.error(resp, 409, "Only completed payments can be refunded");
                return;
            }
            paymentService.hoanTien(payment.getPaymentId());
        } else {
            Map<String, Object> admin = (Map<String, Object>) req.getSession().getAttribute("adminUser");
            String changedBy = admin == null ? "admin" : String.valueOf(admin.get("email"));
            Order order = orderService.timDonHang(orderId);
            if (order != null && order.getDeposit() != null) {
                String stage = ApiUtil.str(body, "stage");
                boolean remaining = "REMAINING".equalsIgnoreCase(stage);
                if (!remaining && stage != null && !"DEPOSIT".equalsIgnoreCase(stage)) {
                    ApiUtil.error(resp, 400, "Invalid collection stage");
                    return;
                }
                try {
                    paymentService.confirmCollectionStage(orderId, remaining, changedBy);
                } catch (IllegalStateException e) {
                    ApiUtil.error(resp, 409, e.getMessage());
                    return;
                }
            } else {
                paymentService.xuLyThanhToan(payment.getPaymentId());
            }
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
        Deposit deposit = order.getDeposit();
        BigDecimal total = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        BigDecimal depositAmount = deposit == null || deposit.getAmount() == null
                ? BigDecimal.ZERO : deposit.getAmount();
        BigDecimal paymentAmount = payment == null || payment.getAmount() == null
                ? BigDecimal.ZERO : payment.getAmount();
        PaymentStatus paymentStatus = payment == null || payment.getPaymentStatus() == null
                ? PaymentStatus.PENDING : payment.getPaymentStatus();
        boolean closed = order.getOrderStatus() == OrderStatus.CANCELLED
                || paymentStatus == PaymentStatus.REFUNDED;
        BigDecimal collected = paymentStatus == PaymentStatus.COMPLETED ? paymentAmount : BigDecimal.ZERO;
        BigDecimal outstanding = closed ? BigDecimal.ZERO : total.subtract(collected).max(BigDecimal.ZERO);

        map.put("id", payment == null ? null : payment.getPaymentId());
        map.put("orderId", order.getOrderId());
        map.put("orderStatus", order.getOrderStatus() == null ? null : order.getOrderStatus().name());
        map.put("method", payment == null || payment.getPaymentMethod() == null
                ? null : payment.getPaymentMethod().name());
        map.put("amount", payment == null ? total : paymentAmount);
        map.put("status", paymentStatus.name());
        map.put("paidAt", payment == null ? null : payment.getPaymentDate());
        map.put("customerName", order.getUser() == null ? "Kh\u00e1ch l\u1ebb" : order.getUser().getFullName());
        map.put("deposit", deposit != null);
        map.put("fullyPaid", paymentStatus == PaymentStatus.COMPLETED && paymentAmount.compareTo(total) >= 0);
        map.put("depositAmount", depositAmount);
        map.put("remainingAmount", total.subtract(depositAmount).max(BigDecimal.ZERO));
        map.put("outstandingAmount", outstanding);
        return map;
    }

}