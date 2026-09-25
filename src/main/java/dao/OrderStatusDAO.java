package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.OrderStatus;

/**
 * DAO quản lý Entity OrderStatus.
 * Vai trò chính: CRUD trạng thái đơn hàng.
 */
public class OrderStatusDAO extends BaseDAO<OrderStatus, String> {

    public OrderStatusDAO() {
        super(OrderStatus.class);
    }

    /** Tìm trạng thái đơn hàng theo tên (ví dụ: "Chờ xử lý", "Đang giao", "Hoàn thành", ...). */
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
