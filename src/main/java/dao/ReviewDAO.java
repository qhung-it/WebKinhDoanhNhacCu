package dao;

import model.entity.Review;

import java.util.List;

/**
 * DAO quản lý Entity Review.
 * Vai trò chính: CRUD đánh giá, tìm theo User/Product.
 */
public interface ReviewDAO extends BaseDAO<Review, String> {

    /** Lấy danh sách đánh giá của một User. */
    List<Review> findByUser(String userId);

    /** Lấy danh sách đánh giá của một sản phẩm. */
    List<Review> findByProduct(String productId);

    /** Lấy đánh giá của một User dành cho một đơn hàng cụ thể (mỗi đơn hàng có thể được review). */
    List<Review> findByOrder(String orderId);

    /** Tính điểm đánh giá trung bình (rating) của một sản phẩm. */
    Double findAverageRatingByProduct(String productId);
}
