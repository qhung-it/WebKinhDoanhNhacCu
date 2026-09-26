package dao;

import model.DepositStatus;
import model.entity.Deposit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DAO quản lý Entity Deposit.
 * Vai trò chính: Tạo/lấy/cập nhật tiền đặt cọc.
 */
public interface DepositDAO extends BaseDAO<Deposit, String> {

    /** Lấy thông tin đặt cọc của một đơn hàng (quan hệ 1-1). */
    Deposit findByOrder(String orderId);

    /** Cập nhật trạng thái đặt cọc (PENDING/COMPLETED). */
    boolean updateStatus(String depositId, DepositStatus status);

    /** Thống kê: đếm số lượng đặt cọc theo trạng thái. */
    long countByStatus(DepositStatus status);

    /** Thống kê: tổng số tiền đặt cọc đã xác nhận (COMPLETED) trong một khoảng thời gian. */
    BigDecimal sumAmountByDateRange(LocalDateTime from, LocalDateTime to);
}
