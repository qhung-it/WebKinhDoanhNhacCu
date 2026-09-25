package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.DepositStatus;
import model.entity.Deposit;

/**
 * DAO quản lý Entity Deposit.
 * Vai trò chính: Tạo/lấy/cập nhật tiền đặt cọc.
 */
public class DepositDAO extends BaseDAO<Deposit, String> {

    public DepositDAO() {
        super(Deposit.class);
    }

    /** Lấy thông tin đặt cọc của một đơn hàng (quan hệ 1-1). */
    public Deposit findByOrder(String orderId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Deposit> query = em.createQuery(
                    "SELECT d FROM Deposit d WHERE d.order.orderId = :orderId", Deposit.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /** Cập nhật trạng thái đặt cọc (PENDING/COMPLETED). */
    public boolean updateStatus(String depositId, DepositStatus status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Deposit deposit = em.find(Deposit.class, depositId);
            if (deposit == null) {
                rollback(em);
                return false;
            }
            deposit.setDepositStatus(status);
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
