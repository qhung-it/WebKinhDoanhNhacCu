package dao;

import model.PaymentStatus;
import model.entity.Payment;

/**
 * DAO quản lý Entity Payment.
 * Vai trò chính: Tạo/lấy/cập nhật thanh toán.
 */
public interface PaymentDAO extends BaseDAO<Payment, String> {

    /** Lấy thông tin thanh toán của một đơn hàng (quan hệ 1-1). */
    Payment findByOrder(String orderId);

    /** Cập nhật trạng thái thanh toán (PENDING/COMPLETED/FAILED). */
    boolean updateStatus(String paymentId, PaymentStatus status);
}
