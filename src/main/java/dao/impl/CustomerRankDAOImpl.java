package dao.impl;

import dao.CustomerRankDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.CustomerRank;

import java.math.BigDecimal;
import java.util.List;

/**
 * Triển khai (implement) CustomerRankDAO.
 */
public class CustomerRankDAOImpl extends BaseDAOImpl<CustomerRank, String> implements CustomerRankDAO {

    public CustomerRankDAOImpl() {
        super(CustomerRank.class);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public long countUsersByRank(String rankId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.customerRank.rankId = :rankId", Long.class);
            query.setParameter("rankId", rankId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
