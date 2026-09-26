package dao.impl;

import dao.OrderDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Triển khai (implement) OrderDAO.
 */
public class OrderDAOImpl extends BaseDAOImpl<Order, String> implements OrderDAO {

    public OrderDAOImpl() {
        super(Order.class);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
