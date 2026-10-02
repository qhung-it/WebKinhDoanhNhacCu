package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.Role;
import model.dao.intf.UserDAO;
import model.entity.User;
import model.util.JpaUtil;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User user) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
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
    public boolean update(User user) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(user);
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
    public boolean delete(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            User user = em.find(User.class, userId);

            if (user == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(user);
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
    public User findById(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            List<User> users = em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email",
                            User.class
                    ).setParameter("email", email)
                    .getResultList();

            return users.isEmpty() ? null : users.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM User u",
                    User.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.role = :role",
                            User.class
                    ).setParameter("role", role)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findByCustomerRankId(String rankId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "WHERE u.customerRank.rankId = :rankId",
                            User.class
                    ).setParameter("rankId", rankId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findByOrderHistory(String userId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "JOIN u.orders o " +
                                    "WHERE u.userId = :userId " +
                                    "ORDER BY o.orderDate DESC",
                            User.class
                    ).setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}