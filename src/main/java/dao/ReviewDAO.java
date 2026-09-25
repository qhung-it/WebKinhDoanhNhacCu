package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Review;

import java.util.List;

/**
 * DAO quản lý Entity Review.
 * Vai trò chính: CRUD đánh giá, tìm theo User/Product.
 */
public class ReviewDAO extends BaseDAO<Review, String> {

    public ReviewDAO() {
        super(Review.class);
    }

    /** Lấy danh sách đánh giá của một User. */
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

    /** Lấy danh sách đánh giá của một sản phẩm. */
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

    /** Lấy đánh giá của một User dành cho một đơn hàng cụ thể (mỗi đơn hàng có thể được review). */
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

    /** Tính điểm đánh giá trung bình (rating) của một sản phẩm. */
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
}
