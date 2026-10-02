package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.CustomerRankDAO;
import model.entity.CustomerRank;
import model.util.JpaUtil;

import java.util.List;

public class CustomerRankDAOImpl implements CustomerRankDAO {

    @Override
    public boolean save(CustomerRank customerRank) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(customerRank);

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
    public boolean update(CustomerRank customerRank) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(customerRank);

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
    public boolean delete(String rankId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            CustomerRank customerRank =
                    em.find(CustomerRank.class, rankId);

            if (customerRank == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(customerRank);

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
    public CustomerRank findById(String rankId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(CustomerRank.class, rankId);

        } finally {
            em.close();
        }
    }

    @Override
    public List<CustomerRank> findAll() {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM CustomerRank c",
                    CustomerRank.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}