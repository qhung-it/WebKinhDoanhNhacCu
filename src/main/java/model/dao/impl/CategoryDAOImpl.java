package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.CategoryDAO;
import model.entity.Category;
import model.util.JpaUtil;

import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    @Override
    public boolean save(Category category) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(category);

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
    public boolean update(Category category) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(category);

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
    public boolean delete(String categoryId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Category category = em.find(Category.class, categoryId);

            if (category == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(category);

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
    public Category findById(String categoryId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Category.class, categoryId);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Category c",
                    Category.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateStatus(String categoryId, boolean status) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Category category = em.find(Category.class, categoryId);

            if (category == null) {
                em.getTransaction().rollback();
                return false;
            }

            category.setStatus(status);

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
}