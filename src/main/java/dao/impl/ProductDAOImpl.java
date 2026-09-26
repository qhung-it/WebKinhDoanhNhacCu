package dao.impl;

import dao.ProductDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Triển khai (implement) ProductDAO.
 */
public class ProductDAOImpl extends BaseDAOImpl<Product, String> implements ProductDAO {

    public ProductDAOImpl() {
        super(Product.class);
    }

    @Override
    public List<Product> searchByName(String keyword) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(:keyword)", Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByCategory(String categoryId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.category.categoryId = :categoryId", Product.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByBrand(String brandId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.brand.brandId = :brandId", Product.class);
            query.setParameter("brandId", brandId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice", Product.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByStatus(String status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.status = :status", Product.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findInStock() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.quantity > 0", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findOutOfStock() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.quantity <= 0", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int getStockQuantity(String productId) {
        Product product = findById(productId);
        return product != null ? product.getQuantity() : 0;
    }

    @Override
    public boolean adjustStock(String productId, int delta) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, productId);
            if (product == null || product.getQuantity() + delta < 0) {
                rollback(em);
                return false;
            }
            product.setQuantity(product.getQuantity() + delta);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public long countAll() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Product p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByCategory(String categoryId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId", Long.class);
            query.setParameter("categoryId", categoryId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByBrand(String brandId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.brand.brandId = :brandId", Long.class);
            query.setParameter("brandId", brandId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByStatus(String status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.status = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
