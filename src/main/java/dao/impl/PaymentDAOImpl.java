package dao.impl;

import dao.PaymentDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.PaymentMethod;
import model.PaymentStatus;
import model.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Triển khai (implement) PaymentDAO.
 */
public class PaymentDAOImpl extends BaseDAOImpl<Payment, String> implements PaymentDAO {

    public PaymentDAOImpl() {
        super(Payment.class);
    }

    @Override
    public Payment findByOrder(String orderId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.order.orderId = :orderId", Payment.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateStatus(String paymentId, PaymentStatus status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Payment payment = em.find(Payment.class, paymentId);
            if (payment == null) {
                rollback(em);
                return false;
            }
            payment.setPaymentStatus(status);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public long countByStatus(PaymentStatus status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal sumAmountByDateRange(LocalDateTime from, LocalDateTime to) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
                            "WHERE p.paymentStatus = model.PaymentStatus.COMPLETED " +
                            "AND p.paymentDate BETWEEN :from AND :to", BigDecimal.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal sumAmountByMethod(PaymentMethod method) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
                            "WHERE p.paymentStatus = model.PaymentStatus.COMPLETED " +
                            "AND p.paymentMethod = :method", BigDecimal.class);
            query.setParameter("method", method);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
