package model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import model.OrderStatus;
import model.dao.intf.OrderDAO;
import model.entity.Order;
import model.entity.OrderLineItem;
import model.entity.Product;
import model.util.JpaUtil;

import java.math.BigDecimal;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public boolean save(Order order) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            for (OrderLineItem item : order.getOrderLineItems()) {
                Product product = em.find(Product.class, item.getProduct().getProductId(),
                        LockModeType.PESSIMISTIC_WRITE);
                if (product == null || item.getQuantity() <= 0
                        || product.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient product stock");
                }
                product.setQuantity(product.getQuantity() - item.getQuantity());
                item.setProduct(product);
            }

            em.persist(order);

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
    public boolean update(Order order) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(order);

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
    public boolean delete(String orderId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);

            if (order == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(order);

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
    public Order findById(String orderId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            Order order = em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.user " +
                            "LEFT JOIN FETCH o.orderLineItems li LEFT JOIN FETCH li.product " +
                            "WHERE o.orderId = :orderId", Order.class)
                    .setParameter("orderId", orderId).getResultStream().findFirst().orElse(null);
            if (order != null) order.getHistory().size();
            return order;

        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByUserId(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            List<Order> orders = em.createQuery(
                            "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.user " +
                                    "LEFT JOIN FETCH o.orderLineItems li LEFT JOIN FETCH li.product " +
                                    "WHERE o.user.userId = :userId",
                            Order.class
                    )
                    .setParameter("userId", userId)
                    .getResultList();
            orders.forEach(o -> o.getHistory().size());
            return orders;

        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {

        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            List<Order> orders = em.createQuery(
                            "SELECT o FROM Order o WHERE o.orderStatus = :status",
                            Order.class
                    )
                    .setParameter("status", status)
                    .getResultList();
            orders.forEach(o -> o.getHistory().size());
            return orders;

        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            List<Order> orders = em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.user " +
                            "LEFT JOIN FETCH o.orderLineItems li LEFT JOIN FETCH li.product",
                    Order.class).getResultList();
            orders.forEach(o -> o.getHistory().size());
            return orders;

        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoSanPham(String productId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            BigDecimal result = em.createQuery(
                            "SELECT SUM(oli.quantity * oli.unitPrice) " +
                                    "FROM OrderLineItem oli " +
                                    "WHERE oli.product.productId = :productId",
                            BigDecimal.class
                    )
                    .setParameter("productId", productId)
                    .getSingleResult();

            return result != null ? result : BigDecimal.ZERO;

        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            BigDecimal result = em.createQuery(
                            "SELECT SUM(oli.quantity * oli.unitPrice) " +
                                    "FROM OrderLineItem oli " +
                                    "WHERE oli.product.category.categoryId = :categoryId",
                            BigDecimal.class
                    )
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();

            return result != null ? result : BigDecimal.ZERO;

        } finally {
            em.close();
        }
    }
}
