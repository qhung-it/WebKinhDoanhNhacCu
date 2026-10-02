package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.DepositDAO;
import model.entity.Deposit;
import model.util.JpaUtil;

public class DepositDAOImpl implements DepositDAO {

    @Override
    public boolean save(Deposit deposit) {

        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(deposit);

            em.getTransaction().commit();

            return true;

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    @Override
    public Deposit findByOrderId(String orderId) {

        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.createQuery(
                            "SELECT d FROM Deposit d " +
                                    "WHERE d.order.orderId = :orderId",
                            Deposit.class
                    )
                    .setParameter("orderId", orderId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }
}