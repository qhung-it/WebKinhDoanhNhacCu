package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.ProductDAO;
import model.entity.Product;
import model.util.JpaUtil;

import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public boolean save(Product product) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(product);

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
    public boolean update(Product product) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(product);

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
    public boolean delete(String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Product product = em.find(Product.class, productId);

            if (product == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(product);

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
    public Product findById(String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Product.class, productId);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Product p",
                    Product.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> filter(String keyword) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Product p " +
                                    "WHERE LOWER(p.productName) LIKE LOWER(:keyword)",
                            Product.class
                    )
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByCategoryId(String categoryId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Product p " +
                                    "WHERE p.category.categoryId = :categoryId",
                            Product.class
                    )
                    .setParameter("categoryId", categoryId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByBrandId(String brandId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Product p " +
                                    "WHERE p.brand.brandId = :brandId",
                            Product.class
                    )
                    .setParameter("brandId", brandId)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}