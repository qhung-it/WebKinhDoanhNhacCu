package dao;

import model.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO quản lý Entity Promotion.
 * Vai trò chính: CRUD khuyến mãi, tìm khuyến mãi áp dụng.
 */
public interface PromotionDAO extends BaseDAO<Promotion, String> {

    /** Lấy danh sách khuyến mãi đang diễn ra tại thời điểm hiện tại. */
    List<Promotion> findActivePromotions(LocalDateTime now);

    /** Tìm các khuyến mãi (đang hoạt động) áp dụng cho một sản phẩm cụ thể. */
    List<Promotion> findApplicablePromotionsForProduct(String productId, LocalDateTime now);

    /** Tìm các khuyến mãi (đang hoạt động) áp dụng cho một hạng khách hàng cụ thể. */
    List<Promotion> findApplicablePromotionsForRank(String rankId, LocalDateTime now);

    /** Tìm khuyến mãi theo tên. */
    List<Promotion> searchByName(String keyword);

    /** Thống kê: đếm số lượng khuyến mãi đang diễn ra tại thời điểm hiện tại. */
    long countActivePromotions(LocalDateTime now);
}
