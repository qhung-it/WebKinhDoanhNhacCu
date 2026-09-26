package dao.impl;

import dao.PaymentDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.PaymentStatus;
import model.entity.Payment;

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
}
