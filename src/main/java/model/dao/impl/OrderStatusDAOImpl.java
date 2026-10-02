package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.OrderStatusDAO;
import model.entity.OrderStatus;
import model.util.JpaUtil;

import java.util.List;

public class OrderStatusDAOImpl implements OrderStatusDAO {

    @Override
    public boolean save(OrderStatus orderStatus) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(orderStatus);

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
    public boolean update(OrderStatus orderStatus) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(orderStatus);

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
    public boolean delete(String statusId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            OrderStatus orderStatus = em.find(OrderStatus.class, statusId);

            if (orderStatus == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(orderStatus);

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
    public OrderStatus findById(String statusId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(OrderStatus.class, statusId);

        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderStatus> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT os FROM OrderStatus os",
                    OrderStatus.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}