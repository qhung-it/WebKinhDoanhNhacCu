package dao;

import model.entity.OrderLineItem;

import java.math.BigDecimal;
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

    /** Thống kê: tổng doanh thu (số lượng x đơn giá) mà một sản phẩm mang lại. */
    BigDecimal sumRevenueByProduct(String productId);

    /**
     * Thống kê: danh sách sản phẩm bán chạy nhất, sắp xếp theo tổng số lượng bán ra giảm dần.
     * Mỗi phần tử trả về là Object[] gồm 2 giá trị: {@code [0] = Product}, {@code [1] = Long} (tổng số lượng đã bán).
     *
     * @param limit số lượng sản phẩm bán chạy muốn lấy (top N)
     */
    List<Object[]> findTopSellingProducts(int limit);
}
