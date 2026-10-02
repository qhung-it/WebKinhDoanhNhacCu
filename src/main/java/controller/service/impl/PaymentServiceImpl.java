package controller.service.impl;

import controller.service.intf.PaymentService;
import model.PaymentStatus;
import model.dao.impl.PaymentDAOImpl;
import model.dao.intf.PaymentDAO;
import model.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentServiceImpl() {
        this.paymentDAO = new PaymentDAOImpl();
    }

    @Override
    public void taoThanhToan(Payment payment) {

        if (payment == null) {
            return;
        }

        if (payment.getPaymentId() == null ||
                payment.getPaymentId().trim().isEmpty()) {
            return;
        }

        if (payment.getOrder() == null) {
            return;
        }

        if (payment.getAmount() == null ||
                payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (payment.getPaymentMethod() == null) {
            return;
        }

        payment.setPaymentStatus(PaymentStatus.PENDING);

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        paymentDAO.save(payment);
    }

    @Override
    public void xuLyThanhToan(String paymentId) {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {
            return;
        }

        Payment payment = paymentDAO.findById(paymentId);

        if (payment == null) {
            return;
        }

        if (payment.getAmount() == null ||
                payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentDAO.update(payment);
            return;
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        paymentDAO.update(payment);
    }

    @Override
    public boolean kiemTraTrangThaiThanhToan(String paymentId) {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {
            return false;
        }

        Payment payment = paymentDAO.findById(paymentId);

        if (payment == null) {
            return false;
        }

        return payment.getPaymentStatus() == PaymentStatus.COMPLETED;
    }

    @Override
    public void capNhatTrangThaiThanhToan(
            String paymentId,
            PaymentStatus status) {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {
            return;
        }

        if (status == null) {
            return;
        }

        Payment payment = paymentDAO.findById(paymentId);

        if (payment == null) {
            return;
        }

        payment.setPaymentStatus(status);

        if (status == PaymentStatus.COMPLETED &&
                payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        paymentDAO.update(payment);
    }

    @Override
    public Payment getThanhToanTheoDonHang(String orderId) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {
            return null;
        }

        return paymentDAO.findByOrderId(orderId);
    }

    @Override
    public void hoanTien(String paymentId) {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {
            return;
        }

        Payment payment = paymentDAO.findById(paymentId);

        if (payment == null) {
            return;
        }

        /*
         * PaymentStatus hiện tại chỉ có:
         * PENDING
         * COMPLETED
         * FAILED
         *
         * Chưa có REFUNDED nên chưa thay đổi
         * trạng thái tại đây.
         */
    }
}