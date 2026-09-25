package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.CartLineItem;

import java.util.List;

/**
 * DAO quản lý Entity CartLineItem.
 * Vai trò chính: CRUD dòng sản phẩm trong Cart.
 */
public class CartLineItemDAO extends BaseDAO<CartLineItem, String> {

    public CartLineItemDAO() {
        super(CartLineItem.class);
    }

    /** Lấy danh sách các dòng sản phẩm trong một giỏ hàng. */
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

    /** Tìm dòng sản phẩm cụ thể trong giỏ hàng theo cartId và productId. */
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

    /** Cập nhật số lượng của một dòng sản phẩm trong giỏ hàng. */
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

    /** Xóa toàn bộ dòng sản phẩm thuộc một giỏ hàng (dùng khi checkout hoặc xóa giỏ hàng). */
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
