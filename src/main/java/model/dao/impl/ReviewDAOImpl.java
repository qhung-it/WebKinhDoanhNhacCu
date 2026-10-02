package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.ReviewDAO;
import model.entity.Review;
import model.util.JpaUtil;

import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public boolean save(Review review) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(review);
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
    public boolean update(Review review) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(review);
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
    public boolean delete(String reviewId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Review review = em.find(Review.class, reviewId);

            if (review == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(review);
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
    public Review findById(String reviewId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Review.class, reviewId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findByProductId(String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Review r " +
                                    "WHERE r.product.productId = :productId",
                            Review.class
                    ).setParameter("productId", productId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findByUserId(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Review r " +
                                    "WHERE r.user.userId = :userId",
                            Review.class
                    ).setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Review r",
                    Review.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}