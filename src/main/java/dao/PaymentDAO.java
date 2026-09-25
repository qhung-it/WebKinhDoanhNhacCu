package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.PaymentStatus;
import model.entity.Payment;

/**
 * DAO quản lý Entity Payment.
 * Vai trò chính: Tạo/lấy/cập nhật thanh toán.
 */
public class PaymentDAO extends BaseDAO<Payment, String> {

    public PaymentDAO() {
        super(Payment.class);
    }

    /** Lấy thông tin thanh toán của một đơn hàng (quan hệ 1-1). */
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

    /** Cập nhật trạng thái thanh toán (PENDING/COMPLETED/FAILED). */
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
