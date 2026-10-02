package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.BrandDAO;
import model.entity.Brand;
import model.util.JpaUtil;

import java.util.List;

public class BrandDAOImpl implements BrandDAO {

    @Override
    public boolean save(Brand brand) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(brand);

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
    public boolean update(Brand brand) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(brand);

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
    public boolean delete(String brandId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Brand brand = em.find(Brand.class, brandId);

            if (brand == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(brand);

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
    public Brand findById(String brandId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Brand.class, brandId);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Brand> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT b FROM Brand b",
                    Brand.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}