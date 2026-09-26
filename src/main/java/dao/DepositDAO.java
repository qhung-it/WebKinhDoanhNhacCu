package dao;

import model.DepositStatus;
import model.entity.Deposit;

/**
 * DAO quản lý Entity Deposit.
 * Vai trò chính: Tạo/lấy/cập nhật tiền đặt cọc.
 */
public interface DepositDAO extends BaseDAO<Deposit, String> {

    /** Lấy thông tin đặt cọc của một đơn hàng (quan hệ 1-1). */
    Deposit findByOrder(String orderId);

    /** Cập nhật trạng thái đặt cọc (PENDING/COMPLETED). */
    boolean updateStatus(String depositId, DepositStatus status);
}
