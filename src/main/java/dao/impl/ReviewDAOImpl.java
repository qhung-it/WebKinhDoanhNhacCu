package dao.impl;

import dao.ReviewDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Review;

import java.util.List;

/**
 * Triển khai (implement) ReviewDAO.
 */
public class ReviewDAOImpl extends BaseDAOImpl<Review, String> implements ReviewDAO {

    public ReviewDAOImpl() {
        super(Review.class);
    }

    @Override
    public List<Review> findByUser(String userId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.user.userId = :userId", Review.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.product.productId = :productId", Review.class);
            query.setParameter("productId", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findByOrder(String orderId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.order.orderId = :orderId", Review.class);
            query.setParameter("orderId", orderId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Double findAverageRatingByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId", Double.class);
            query.setParameter("productId", productId);
            Double result = query.getSingleResult();
            return result != null ? result : 0.0;
        } finally {
            em.close();
        }
    }

    @Override
    public long countByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId", Long.class);
            query.setParameter("productId", productId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByProductAndRating(String productId, int rating) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId " +
                            "AND r.rating = :rating", Long.class);
            query.setParameter("productId", productId);
            query.setParameter("rating", rating);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
