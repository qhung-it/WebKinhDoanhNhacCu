package dao.impl;

import dao.BaseDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import model.util.JpaUtil;

import java.util.List;

/**
 * Lớp trừu tượng triển khai (implement) các thao tác CRUD dùng chung khai báo
 * trong {@link BaseDAO}. Các lớp Impl cụ thể (UserDAOImpl, ProductDAOImpl, ...)
 * kế thừa lớp này để tái sử dụng code, tránh lặp lại ở từng lớp.
 *
 * @param <T>  Kiểu Entity (User, Product, ...)
 * @param <ID> Kiểu khóa chính của Entity (thường là String)
 */
public abstract class BaseDAOImpl<T, ID> implements BaseDAO<T, ID> {

    protected final Class<T> entityClass;

    protected BaseDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManagerFactory getEmf() {
        return JpaUtil.getEntityManagerFactory();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T findById(ID id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(
                    "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteById(ID id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return entity != null;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    protected void rollback(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
