package model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import model.dao.intf.UserFeatureDAO;
import model.entity.ContactMessage;
import model.entity.NewsletterSubscription;
import model.entity.WishlistItem;
import model.util.JpaUtil;
import java.util.List;

public class UserFeatureDAOImpl implements UserFeatureDAO {
    @Override
    public List<WishlistItem> findWishlistByUserId(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user.userId = :id", WishlistItem.class)
                    .setParameter("id", userId).getResultList();
        } finally { em.close(); }
    }

    @Override
    public boolean addWishlistItem(String userId, String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Long count = em.createQuery("SELECT COUNT(w) FROM WishlistItem w WHERE w.user.userId = :u AND w.product.productId = :p", Long.class)
                    .setParameter("u", userId).setParameter("p", productId).getSingleResult();
            if (count == 0) {
                WishlistItem item = new WishlistItem();
                item.setWishlistItemId(java.util.UUID.randomUUID().toString());
                item.setUser(em.getReference(model.entity.User.class, userId));
                item.setProduct(em.getReference(model.entity.Product.class, productId));
                em.persist(item);
            }
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    @Override
    public boolean removeWishlistItem(String userId, String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM WishlistItem w WHERE w.user.userId = :u AND w.product.productId = :p")
                    .setParameter("u", userId).setParameter("p", productId).executeUpdate();
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    @Override
    public boolean saveContact(ContactMessage message) {
        return persist(message);
    }

    @Override
    public boolean subscribe(NewsletterSubscription subscription) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(n) FROM NewsletterSubscription n WHERE LOWER(n.email) = LOWER(:email)", Long.class)
                    .setParameter("email", subscription.getEmail()).getSingleResult();
            if (count > 0) return true;
            em.getTransaction().begin();
            em.persist(subscription);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }

    private boolean persist(Object entity) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally { em.close(); }
    }
}
