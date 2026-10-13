package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.CartDAO;
import model.entity.Cart;
import model.util.JpaUtil;

public class CartDAOImpl implements CartDAO {

    @Override
    public boolean save(Cart cart) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cart);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally { em.close(); }
    }

    @Override
    public boolean update(Cart cart) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cart);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally { em.close(); }
    }

    @Override
    public Cart findByUserId(String userId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            Cart cart = em.createQuery(
                            "SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.cartLineItems li LEFT JOIN FETCH li.product WHERE c.user.userId = :userId",
                            Cart.class
                    )
                    .setParameter("userId", userId)
                    .getResultStream().findFirst().orElse(null);
            return cart;

        } finally {
            em.close();
        }
    }
}
