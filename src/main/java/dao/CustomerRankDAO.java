package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.CustomerRank;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO quản lý Entity CustomerRank.
 * Vai trò chính: CRUD hạng khách hàng, tìm hạng.
 */
public class CustomerRankDAO extends BaseDAO<CustomerRank, String> {

    public CustomerRankDAO() {
        super(CustomerRank.class);
    }

    /** Tìm hạng khách hàng theo tên hạng. */
    public CustomerRank findByRankName(String rankName) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<CustomerRank> query = em.createQuery(
                    "SELECT r FROM CustomerRank r WHERE r.rankName = :rankName", CustomerRank.class);
            query.setParameter("rankName", rankName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Xác định hạng phù hợp nhất dựa trên tổng chi tiêu của khách hàng
     * (hạng có min_spending lớn nhất mà vẫn <= tổng chi tiêu).
     */
    public CustomerRank findRankBySpending(BigDecimal totalSpending) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<CustomerRank> query = em.createQuery(
                    "SELECT r FROM CustomerRank r WHERE r.minSpending <= :spending " +
                            "ORDER BY r.minSpending DESC", CustomerRank.class);
            query.setParameter("spending", totalSpending);
            List<CustomerRank> results = query.setMaxResults(1).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách hạng khách hàng sắp xếp theo mức chi tiêu tối thiểu tăng dần. */
    public List<CustomerRank> findAllOrderByMinSpending() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<CustomerRank> query = em.createQuery(
                    "SELECT r FROM CustomerRank r ORDER BY r.minSpending ASC", CustomerRank.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
