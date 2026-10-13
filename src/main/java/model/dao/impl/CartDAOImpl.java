package model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import model.dao.intf.CartDAO;
import model.entity.Cart;
import model.entity.CartLineItem;
import model.entity.Product;
import model.entity.User;
import model.util.JpaUtil;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartDAOImpl implements CartDAO {
    private static final Logger LOGGER = Logger.getLogger(CartDAOImpl.class.getName());
    @Override
    public boolean save(Cart cart) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cart);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Unable to save cart", e);
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
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Unable to update cart", e);
            return false;
        } finally { em.close(); }
    }

    @Override
    public Cart findByUserId(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.cartLineItems i LEFT JOIN FETCH i.product WHERE c.user.userId = :userId", Cart.class)
                    .setParameter("userId", userId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally { em.close(); }
    }

    @Override
    public boolean addItem(String userId, String productId, int quantity) {
        if (quantity <= 0) return false;
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.getReference(User.class, userId);
            Product product = em.find(Product.class, productId);
            if (product == null || product.getQuantity() < quantity) { em.getTransaction().rollback(); return false; }
            Cart cart = findOrCreate(em, user);
            CartLineItem line = null;
            for (CartLineItem candidate : cart.getCartLineItems()) if (candidate.getProduct().getProductId().equals(productId)) { line = candidate; break; }
            if (line == null) {
                line = new CartLineItem(); line.setCartLineItemId(UUID.randomUUID().toString()); line.setCart(cart); line.setProduct(product); line.setQuantity(quantity); em.persist(line);
            } else {
                if (line.getQuantity() + quantity > product.getQuantity()) { em.getTransaction().rollback(); return false; }
                line.setQuantity(line.getQuantity() + quantity);
            }
            em.getTransaction().commit(); return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    @Override
    public boolean updateItem(String userId, String productId, int quantity) {
        if (quantity <= 0) return removeItem(userId, productId);
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, productId);
            if (product == null || product.getQuantity() < quantity) {
                em.getTransaction().rollback();
                return false;
            }
            int updated = em.createQuery("UPDATE CartLineItem i SET i.quantity = :quantity WHERE i.cart.user.userId = :userId AND i.product.productId = :productId")
                    .setParameter("quantity", quantity)
                    .setParameter("userId", userId)
                    .setParameter("productId", productId)
                    .executeUpdate();
            if (updated == 0) {
                em.getTransaction().rollback();
                return false;
            }
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Unable to update cart quantity (userId=" + userId + ", productId=" + productId + ", quantity=" + quantity + ")", e);
            return false;
        } finally { em.close(); }
    }

    @Override
    public boolean removeItem(String userId, String productId) {
        return executeDelete("DELETE FROM CartLineItem i WHERE i.cart.user.userId = :u AND i.product.productId = :p", userId, productId);
    }

    @Override
    public boolean clear(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CartLineItem i WHERE i.cart.user.userId = :u").setParameter("u", userId).executeUpdate();
            em.getTransaction().commit(); return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    private boolean executeDelete(String jpql, String userId, String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(jpql).setParameter("u", userId).setParameter("p", productId).executeUpdate();
            em.getTransaction().commit(); return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    private Cart findOrCreate(EntityManager em, User user) {
        try {
            return em.createQuery("SELECT c FROM Cart c LEFT JOIN FETCH c.cartLineItems i LEFT JOIN FETCH i.product WHERE c.user.userId = :id", Cart.class)
                    .setParameter("id", user.getUserId()).getSingleResult();
        } catch (NoResultException e) {
            Cart cart = new Cart(); cart.setCartId(UUID.randomUUID().toString()); cart.setUser(user); em.persist(cart); return cart;
        }
    }
}
