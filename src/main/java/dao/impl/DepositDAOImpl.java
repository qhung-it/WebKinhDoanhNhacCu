package dao.impl;

import dao.DepositDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.DepositStatus;
import model.entity.Deposit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Triển khai (implement) DepositDAO.
 */
public class DepositDAOImpl extends BaseDAOImpl<Deposit, String> implements DepositDAO {

    public DepositDAOImpl() {
        super(Deposit.class);
    }

    @Override
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

    @Override
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

    @Override
    public long countByStatus(DepositStatus status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(d) FROM Deposit d WHERE d.depositStatus = :status", Long.class);
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
                    "SELECT COALESCE(SUM(d.amount), 0) FROM Deposit d " +
                            "WHERE d.depositStatus = model.DepositStatus.COMPLETED " +
                            "AND d.depositDate BETWEEN :from AND :to", BigDecimal.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
