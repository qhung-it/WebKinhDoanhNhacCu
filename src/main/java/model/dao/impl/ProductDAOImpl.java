package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.ProductDAO;
import model.entity.Product;
import model.util.JpaUtil;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
            return em.createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE p.productId = :id", Product.class)
                    .setParameter("id", productId).getResultStream().findFirst().orElse(null);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand",
                    Product.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Map<String, Long> findCompletedSalesCounts() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Object[]> rows = em.createQuery(
                    "SELECT item.product.productId, SUM(item.quantity) FROM OrderLineItem item " +
                            "WHERE item.order.orderStatus = :status GROUP BY item.product.productId", Object[].class)
                    .setParameter("status", model.OrderStatus.COMPLETED)
                    .getResultList();
            Map<String, Long> result = new HashMap<>();
            for (Object[] row : rows) result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public Map<String, BigDecimal> findActivePromotionPrices() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Object[]> rows = em.createQuery(
                    "SELECT product.productId, product.price, promotion.discountType, promotion.discountValue, promotion.maxDiscount " +
                            "FROM Promotion promotion JOIN promotion.products product " +
                            "WHERE promotion.active = true AND promotion.couponCode IS NULL " +
                            "AND SIZE(promotion.customerRanks) = 0 " +
                            "AND (promotion.startDateTime IS NULL OR promotion.startDateTime <= :now) " +
                            "AND (promotion.endDateTime IS NULL OR promotion.endDateTime >= :now)", Object[].class)
                    .setParameter("now", now)
                    .getResultList();
            Map<String, BigDecimal> result = new HashMap<>();
            for (Object[] row : rows) {
                String productId = String.valueOf(row[0]);
                BigDecimal price = (BigDecimal) row[1];
                BigDecimal value = (BigDecimal) row[3];
                if (price == null || value == null || price.signum() <= 0 || value.signum() <= 0) continue;
                BigDecimal discount = row[2] == model.DiscountType.PERCENTAGE
                        ? price.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        : value;
                BigDecimal maxDiscount = (BigDecimal) row[4];
                if (maxDiscount != null && maxDiscount.signum() > 0 && discount.compareTo(maxDiscount) > 0)
                    discount = maxDiscount;
                if (discount.compareTo(price) > 0) discount = price;
                BigDecimal candidate = price.subtract(discount);
                BigDecimal current = result.get(productId);
                if (candidate.signum() >= 0 && (current == null || candidate.compareTo(current) < 0))
                    result.put(productId, candidate);
            }
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> filter(String keyword) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
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
                            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
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
                            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
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
