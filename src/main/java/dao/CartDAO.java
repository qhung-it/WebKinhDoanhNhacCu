package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Cart;

/**
 * DAO quản lý Entity Cart.
 * Vai trò chính: Lấy/xóa giỏ hàng theo User.
 */
public class CartDAO extends BaseDAO<Cart, String> {

    public CartDAO() {
        super(Cart.class);
    }

    /** Lấy giỏ hàng của một User (mỗi User chỉ có 1 Cart - quan hệ 1-1). */
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

    /** Xóa giỏ hàng của một User (đồng thời xóa các CartLineItem nhờ cascade REMOVE). */
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
