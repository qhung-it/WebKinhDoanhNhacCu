package dao.impl;

import dao.OrderStatusDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.OrderStatus;

/**
 * Triển khai (implement) OrderStatusDAO.
 */
public class OrderStatusDAOImpl extends BaseDAOImpl<OrderStatus, String> implements OrderStatusDAO {

    public OrderStatusDAOImpl() {
        super(OrderStatus.class);
    }

    @Override
    public OrderStatus findByStatusName(String statusName) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<OrderStatus> query = em.createQuery(
                    "SELECT s FROM OrderStatus s WHERE s.statusName = :statusName", OrderStatus.class);
            query.setParameter("statusName", statusName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
