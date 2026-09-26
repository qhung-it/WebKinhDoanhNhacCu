package dao;

import model.entity.OrderStatus;

/**
 * DAO quản lý Entity OrderStatus.
 * Vai trò chính: CRUD trạng thái đơn hàng.
 */
public interface OrderStatusDAO extends BaseDAO<OrderStatus, String> {

    /** Tìm trạng thái đơn hàng theo tên (ví dụ: "Chờ xử lý", "Đang giao", "Hoàn thành", ...). */
    OrderStatus findByStatusName(String statusName);
}
