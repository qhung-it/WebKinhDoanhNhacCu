package dao;

import model.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO quản lý Entity Order.
 * Vai trò chính: CRUD đơn hàng, tìm theo User/trạng thái, thống kê.
 */
public interface OrderDAO extends BaseDAO<Order, String> {

    /** Lấy danh sách đơn hàng của một User, mới nhất trước. */
    List<Order> findByUser(String userId);

    /** Lấy danh sách đơn hàng theo trạng thái đơn hàng. */
    List<Order> findByStatus(String statusId);

    /** Lấy danh sách đơn hàng của một User lọc theo trạng thái. */
    List<Order> findByUserAndStatus(String userId, String statusId);

    /** Lấy danh sách đơn hàng trong một khoảng thời gian (dùng cho thống kê). */
    List<Order> findByDateRange(LocalDateTime from, LocalDateTime to);

    /** Thống kê: đếm số lượng đơn hàng theo trạng thái. */
    long countByStatus(String statusId);

    /** Thống kê: tổng doanh thu (tổng total_amount) trong một khoảng thời gian. */
    BigDecimal sumRevenueByDateRange(LocalDateTime from, LocalDateTime to);

    /** Thống kê: tổng số tiền một User đã chi tiêu (dùng để xét hạng khách hàng). */
    BigDecimal sumSpendingByUser(String userId);
}
