package dao;

import model.PaymentMethod;
import model.PaymentStatus;
import model.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DAO quản lý Entity Payment.
 * Vai trò chính: Tạo/lấy/cập nhật thanh toán.
 */
public interface PaymentDAO extends BaseDAO<Payment, String> {

    /** Lấy thông tin thanh toán của một đơn hàng (quan hệ 1-1). */
    Payment findByOrder(String orderId);

    /** Cập nhật trạng thái thanh toán (PENDING/COMPLETED/FAILED). */
    boolean updateStatus(String paymentId, PaymentStatus status);

    /** Thống kê: đếm số lượng thanh toán theo trạng thái. */
    long countByStatus(PaymentStatus status);

    /** Thống kê: tổng số tiền đã thanh toán thành công (COMPLETED) trong một khoảng thời gian. */
    BigDecimal sumAmountByDateRange(LocalDateTime from, LocalDateTime to);

    /** Thống kê: tổng số tiền đã thanh toán thành công (COMPLETED) theo phương thức thanh toán. */
    BigDecimal sumAmountByMethod(PaymentMethod method);
}
