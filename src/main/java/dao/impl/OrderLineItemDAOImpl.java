package dao.impl;

import dao.OrderLineItemDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.OrderLineItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Triển khai (implement) OrderLineItemDAO.
 */
public class OrderLineItemDAOImpl extends BaseDAOImpl<OrderLineItem, String> implements OrderLineItemDAO {

    public OrderLineItemDAOImpl() {
        super(OrderLineItem.class);
    }

    @Override
    public List<OrderLineItem> findByOrder(String orderId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<OrderLineItem> query = em.createQuery(
                    "SELECT oi FROM OrderLineItem oi WHERE oi.order.orderId = :orderId", OrderLineItem.class);
            query.setParameter("orderId", orderId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderLineItem> findByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<OrderLineItem> query = em.createQuery(
                    "SELECT oi FROM OrderLineItem oi WHERE oi.product.productId = :productId", OrderLineItem.class);
            query.setParameter("productId", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long sumQuantitySoldByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderLineItem oi " +
                            "WHERE oi.product.productId = :productId", Long.class);
            query.setParameter("productId", productId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal sumRevenueByProduct(String productId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT COALESCE(SUM(oi.quantity * oi.unitPrice), 0) FROM OrderLineItem oi " +
                            "WHERE oi.product.productId = :productId", BigDecimal.class);
            query.setParameter("productId", productId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Object[]> findTopSellingProducts(int limit) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT oi.product, SUM(oi.quantity) AS totalSold FROM OrderLineItem oi " +
                            "GROUP BY oi.product ORDER BY totalSold DESC", Object[].class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
