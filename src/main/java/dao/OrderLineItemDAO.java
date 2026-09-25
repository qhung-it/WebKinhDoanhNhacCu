package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.OrderLineItem;

import java.util.List;

/**
 * DAO quản lý Entity OrderLineItem.
 * Vai trò chính: CRUD chi tiết đơn hàng.
 */
public class OrderLineItemDAO extends BaseDAO<OrderLineItem, String> {

    public OrderLineItemDAO() {
        super(OrderLineItem.class);
    }

    /** Lấy danh sách các dòng chi tiết thuộc một đơn hàng. */
    public List<OrderLineItem> findByOrder(String orderId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<OrderLineItem> query = em.createQuery(
                    "SELECT oi FROM OrderLineItem oi WHERE oi.order.orderId = :orderId", OrderLineItem.class);
            query.setParameter("orderId", orderId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách các dòng chi tiết đơn hàng có chứa một sản phẩm cụ thể (dùng cho thống kê sản phẩm bán chạy). */
    public List<OrderLineItem> findByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<OrderLineItem> query = em.createQuery(
                    "SELECT oi FROM OrderLineItem oi WHERE oi.product.productId = :productId", OrderLineItem.class);
            query.setParameter("productId", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Tính tổng số lượng đã bán ra của một sản phẩm. */
    public long sumQuantitySoldByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderLineItem oi " +
                            "WHERE oi.product.productId = :productId", Long.class);
            query.setParameter("productId", productId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
