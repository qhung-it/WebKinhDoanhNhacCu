package dao.impl;

import dao.CartLineItemDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.CartLineItem;

import java.util.List;

/**
 * Triển khai (implement) CartLineItemDAO.
 */
public class CartLineItemDAOImpl extends BaseDAOImpl<CartLineItem, String> implements CartLineItemDAO {

    public CartLineItemDAOImpl() {
        super(CartLineItem.class);
    }

    @Override
    public List<CartLineItem> findByCart(String cartId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<CartLineItem> query = em.createQuery(
                    "SELECT ci FROM CartLineItem ci WHERE ci.cart.cartId = :cartId", CartLineItem.class);
            query.setParameter("cartId", cartId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public CartLineItem findByCartAndProduct(String cartId, String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<CartLineItem> query = em.createQuery(
                    "SELECT ci FROM CartLineItem ci WHERE ci.cart.cartId = :cartId " +
                            "AND ci.product.productId = :productId", CartLineItem.class);
            query.setParameter("cartId", cartId);
            query.setParameter("productId", productId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateQuantity(String cartLineItemId, int quantity) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            CartLineItem item = em.find(CartLineItem.class, cartLineItemId);
            if (item == null) {
                rollback(em);
                return false;
            }
            item.setQuantity(quantity);
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
    public void deleteAllByCart(String cartId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CartLineItem ci WHERE ci.cart.cartId = :cartId")
                    .setParameter("cartId", cartId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }
}
