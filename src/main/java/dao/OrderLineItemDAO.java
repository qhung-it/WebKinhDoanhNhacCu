package dao;

import model.entity.OrderLineItem;

import java.util.List;

/**
 * DAO quản lý Entity OrderLineItem.
 * Vai trò chính: CRUD chi tiết đơn hàng.
 */
public interface OrderLineItemDAO extends BaseDAO<OrderLineItem, String> {

    /** Lấy danh sách các dòng chi tiết thuộc một đơn hàng. */
    List<OrderLineItem> findByOrder(String orderId);

    /** Lấy danh sách các dòng chi tiết đơn hàng có chứa một sản phẩm cụ thể (dùng cho thống kê sản phẩm bán chạy). */
    List<OrderLineItem> findByProduct(String productId);

    /** Tính tổng số lượng đã bán ra của một sản phẩm. */
    long sumQuantitySoldByProduct(String productId);
}
