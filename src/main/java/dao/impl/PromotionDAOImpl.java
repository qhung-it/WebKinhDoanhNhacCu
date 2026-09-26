package dao.impl;

import dao.PromotionDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Triển khai (implement) PromotionDAO.
 */
public class PromotionDAOImpl extends BaseDAOImpl<Promotion, String> implements PromotionDAO {

    public PromotionDAOImpl() {
        super(Promotion.class);
    }

    @Override
    public List<Promotion> findActivePromotions(LocalDateTime now) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                    "SELECT p FROM Promotion p WHERE p.startDateTime <= :now AND p.endDateTime >= :now",
                    Promotion.class);
            query.setParameter("now", now);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findApplicablePromotionsForProduct(String productId, LocalDateTime now) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                    "SELECT p FROM Promotion p JOIN p.products pr " +
                            "WHERE pr.productId = :productId " +
                            "AND p.startDateTime <= :now AND p.endDateTime >= :now", Promotion.class);
            query.setParameter("productId", productId);
            query.setParameter("now", now);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findApplicablePromotionsForRank(String rankId, LocalDateTime now) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                    "SELECT p FROM Promotion p JOIN p.customerRanks cr " +
                            "WHERE cr.rankId = :rankId " +
                            "AND p.startDateTime <= :now AND p.endDateTime >= :now", Promotion.class);
            query.setParameter("rankId", rankId);
            query.setParameter("now", now);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> searchByName(String keyword) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                    "SELECT p FROM Promotion p WHERE LOWER(p.promotionName) LIKE LOWER(:keyword)",
                    Promotion.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
