package dao;

import model.entity.CustomerRank;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO quản lý Entity CustomerRank.
 * Vai trò chính: CRUD hạng khách hàng, tìm hạng.
 */
public interface CustomerRankDAO extends BaseDAO<CustomerRank, String> {

    /** Tìm hạng khách hàng theo tên hạng. */
    CustomerRank findByRankName(String rankName);

    /**
     * Xác định hạng phù hợp nhất dựa trên tổng chi tiêu của khách hàng
     * (hạng có min_spending lớn nhất mà vẫn <= tổng chi tiêu).
     */
    CustomerRank findRankBySpending(BigDecimal totalSpending);

    /** Lấy danh sách hạng khách hàng sắp xếp theo mức chi tiêu tối thiểu tăng dần. */
    List<CustomerRank> findAllOrderByMinSpending();

    /** Thống kê: đếm số lượng khách hàng (User) đang thuộc một hạng cụ thể. */
    long countUsersByRank(String rankId);
}
