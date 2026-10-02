package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.PromotionDAO;
import model.entity.Promotion;
import model.util.JpaUtil;

import java.util.List;

public class PromotionDAOImpl implements PromotionDAO {

    @Override
    public boolean save(Promotion promotion) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(promotion);
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
    public boolean update(Promotion promotion) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(promotion);
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
    public boolean delete(String promotionId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Promotion promotion = em.find(Promotion.class, promotionId);

            if (promotion == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(promotion);
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
    public Promotion findById(String promotionId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Promotion.class, promotionId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Promotion p",
                    Promotion.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findByProductId(String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Promotion p " +
                                    "JOIN p.products product " +
                                    "WHERE product.productId = :productId",
                            Promotion.class
                    ).setParameter("productId", productId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findByCustomerRankId(String rankId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Promotion p " +
                                    "JOIN p.customerRanks rank " +
                                    "WHERE rank.rankId = :rankId",
                            Promotion.class
                    ).setParameter("rankId", rankId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}