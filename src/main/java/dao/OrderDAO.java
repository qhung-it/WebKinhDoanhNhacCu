package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO quản lý Entity Order.
 * Vai trò chính: CRUD đơn hàng, tìm theo User/trạng thái, thống kê.
 */
public class OrderDAO extends BaseDAO<Order, String> {

    public OrderDAO() {
        super(Order.class);
    }

    /** Lấy danh sách đơn hàng của một User, mới nhất trước. */
    public List<Order> findByUser(String userId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.user.userId = :userId ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách đơn hàng theo trạng thái đơn hàng. */
    public List<Order> findByStatus(String statusId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.orderStatus.statusId = :statusId", Order.class);
            query.setParameter("statusId", statusId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách đơn hàng của một User lọc theo trạng thái. */
    public List<Order> findByUserAndStatus(String userId, String statusId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.user.userId = :userId " +
                            "AND o.orderStatus.statusId = :statusId ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("userId", userId);
            query.setParameter("statusId", statusId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách đơn hàng trong một khoảng thời gian (dùng cho thống kê). */
    public List<Order> findByDateRange(LocalDateTime from, LocalDateTime to) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.orderDate BETWEEN :from AND :to " +
                            "ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Thống kê: đếm số lượng đơn hàng theo trạng thái. */
    public long countByStatus(String statusId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.orderStatus.statusId = :statusId", Long.class);
            query.setParameter("statusId", statusId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Thống kê: tổng doanh thu (tổng total_amount) trong một khoảng thời gian. */
    public BigDecimal sumRevenueByDateRange(LocalDateTime from, LocalDateTime to) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                            "WHERE o.orderDate BETWEEN :from AND :to", BigDecimal.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Thống kê: tổng số tiền một User đã chi tiêu (dùng để xét hạng khách hàng). */
    public BigDecimal sumSpendingByUser(String userId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                            "WHERE o.user.userId = :userId", BigDecimal.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
