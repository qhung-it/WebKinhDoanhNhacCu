package dao.impl;

import dao.CartDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Cart;

/**
 * Triển khai (implement) CartDAO.
 */
public class CartDAOImpl extends BaseDAOImpl<Cart, String> implements CartDAO {

    public CartDAOImpl() {
        super(Cart.class);
    }

    @Override
    public Cart findByUser(String userId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Cart> query = em.createQuery(
                    "SELECT c FROM Cart c WHERE c.user.userId = :userId", Cart.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteByUser(String userId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Cart> query = em.createQuery(
                    "SELECT c FROM Cart c WHERE c.user.userId = :userId", Cart.class);
            query.setParameter("userId", userId);
            Cart cart;
            try {
                cart = query.getSingleResult();
            } catch (NoResultException e) {
                cart = null;
            }
            if (cart != null) {
                em.remove(cart);
            }
            em.getTransaction().commit();
            return cart != null;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }
}
