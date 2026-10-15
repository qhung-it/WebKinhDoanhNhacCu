package controller.service.impl;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.PaymentStatus;
import model.entity.Order;
import model.entity.OrderLineItem;
import model.entity.Payment;
import model.entity.Product;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Sends one paid-order receipt to the email on the customer's account. */
public class PaymentReceiptEmailService {
    private static final Logger LOG = Logger.getLogger(PaymentReceiptEmailService.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PaymentServiceImpl payments = new PaymentServiceImpl();
    private final OrderServiceImpl orders = new OrderServiceImpl();

    /** Sends an order invoice after checkout, even while payment is pending. */
    public void sendOrderConfirmation(String orderId) {
        try {
            Order order = orders.timDonHang(orderId);
            if (order == null || order.getUser() == null || blank(order.getUser().getEmail())) {
                LOG.warning("Cannot send order confirmation: order/customer email is missing for " + orderId);
                return;
            }
            Payment payment = payments.getThanhToanTheoDonHang(orderId);
            boolean paid = payment != null && payment.getPaymentStatus() == PaymentStatus.COMPLETED;
            send(order.getUser().getEmail(), order, paid);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not send order confirmation for " + orderId, e);
        }
    }

    public void sendIfPaid(String orderId) {
        try {
            Payment payment = payments.getThanhToanTheoDonHang(orderId);
            if (payment == null || payment.getPaymentStatus() != PaymentStatus.COMPLETED || payment.isReceiptSent()) {
                return;
            }

            Order order = orders.timDonHang(orderId);
            if (order == null || order.getUser() == null || blank(order.getUser().getEmail())) {
                LOG.warning("Cannot send payment receipt: order/customer email is missing for " + orderId);
                return;
            }

            send(order.getUser().getEmail(), order, true);
            payment.setReceiptSent(true);
            if (!payments.markReceiptSent(payment)) {
                LOG.warning("Receipt was sent but could not be marked as sent for order " + orderId);
            }
        } catch (Exception e) {
            // Email delivery must not undo or reject a payment already recorded as completed.
            LOG.log(Level.SEVERE, "Could not send paid-order receipt for " + orderId, e);
        }
    }

    private void send(String recipient, Order order, boolean paid) throws Exception {
        String host = env("SOUNDORA_SMTP_HOST", "smtp.gmail.com");
        String port = env("SOUNDORA_SMTP_PORT", "587");
        String username = env("SOUNDORA_SMTP_USERNAME", "");
        String password = env("SOUNDORA_SMTP_PASSWORD", "");
        String from = env("SOUNDORA_SMTP_FROM", username);
        if (blank(username) || blank(password) || blank(from)) {
            throw new IllegalStateException("SMTP is not configured. Set SOUNDORA_SMTP_USERNAME and SOUNDORA_SMTP_PASSWORD.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from, "Soundora", StandardCharsets.UTF_8.name()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject((paid ? "Biên nhận thanh toán" : "Hóa đơn đơn hàng")
                + " " + order.getOrderId(), StandardCharsets.UTF_8.name());
        message.setContent(renderHtml(order, paid), "text/html; charset=UTF-8");
        Transport.send(message);
    }

    private String renderHtml(Order order, boolean paid) {
        NumberFormat money = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        StringBuilder rows = new StringBuilder();
        for (OrderLineItem line : order.getOrderLineItems()) {
            Product product = line.getProduct();
            String name = product == null ? "Sản phẩm" : product.getProductName();
            BigDecimal price = line.getUnitPrice() != null
                    ? line.getUnitPrice()
                    : product == null || product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(line.getQuantity()));
            rows.append("<tr><td style='padding:8px;border-bottom:1px solid #eee'>")
                    .append(escape(name)).append("</td><td style='padding:8px;text-align:center;border-bottom:1px solid #eee'>")
                    .append(line.getQuantity()).append("</td><td style='padding:8px;text-align:right;border-bottom:1px solid #eee'>")
                    .append(money.format(price)).append(" đ</td><td style='padding:8px;text-align:right;border-bottom:1px solid #eee'>")
                    .append(money.format(lineTotal)).append(" đ</td></tr>");
        }

        String date = order.getOrderDate() == null ? "" : order.getOrderDate().format(DATE_FORMAT);
        return "<div style='font-family:Arial,sans-serif;color:#24243a;max-width:720px;margin:auto'>"
                + "<h2 style='color:#6c5ce7'>Soundora · "
                + (paid ? "Biên nhận thanh toán" : "Hóa đơn đơn hàng") + "</h2>"
                + "<p>Đơn hàng <b>#" + escape(order.getOrderId()) + "</b> "
                + (paid ? "đã được xác nhận thanh toán thành công."
                : "đã được tiếp nhận thành công. Trạng thái thanh toán: chờ xác nhận.") + "</p>"
                + "<p>Ngày đặt: " + escape(date) + "<br>Người nhận: "
                + escape(order.getUser().getFullName()) + "<br>Địa chỉ: "
                + escape(order.getShippingAddress()) + "</p>"
                + "<table style='width:100%;border-collapse:collapse'><thead><tr>"
                + "<th style='text-align:left;padding:8px'>Sản phẩm</th><th>Số lượng</th>"
                + "<th style='text-align:right'>Đơn giá</th><th style='text-align:right'>Thành tiền</th>"
                + "</tr></thead><tbody>" + rows + "</tbody></table>"
                + "<p style='text-align:right'>Tạm tính: " + money.format(value(order.getSubtotal())) + " đ<br>"
                + "Giảm giá: -" + money.format(value(order.getDiscount())) + " đ<br>"
                + "Phí giao hàng: " + money.format(value(order.getShippingFee())) + " đ<br>"
                + (paid ? "<b>Tổng đã thanh toán: " : "<b>Tổng đơn hàng: ")
                + money.format(value(order.getTotalAmount())) + " đ</b></p>"
                + "<p>Cảm ơn bạn đã mua sắm tại Soundora.</p></div>";
    }

    private static BigDecimal value(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
