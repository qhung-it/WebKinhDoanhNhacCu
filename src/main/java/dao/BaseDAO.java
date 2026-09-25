package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import model.util.JpaUtil;

import java.util.List;

/**
 * Lớp DAO cơ sở, cung cấp các thao tác CRUD dùng chung cho mọi Entity.
 * Các DAO cụ thể (UserDAO, ProductDAO, ...) kế thừa lớp này và bổ sung
 * thêm các phương thức tìm kiếm/truy vấn đặc thù cho từng Entity.
 *
 * @param <T>  Kiểu Entity (User, Product, ...)
 * @param <ID> Kiểu khóa chính của Entity (thường là String)
 */
public abstract class BaseDAO<T, ID> {

    protected final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManagerFactory getEmf() {
        return JpaUtil.getEntityManagerFactory();
    }

    /** Thêm mới một Entity vào database. */
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

    /** Cập nhật một Entity đã tồn tại. */
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

    /** Tìm một Entity theo khóa chính. Trả về null nếu không tìm thấy. */
    public T findById(ID id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    /** Lấy toàn bộ danh sách Entity. */
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

    /** Xóa một Entity (theo tham chiếu đối tượng). */
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

    /** Xóa một Entity theo khóa chính. Trả về true nếu xóa thành công. */
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

    /** Đếm tổng số bản ghi hiện có. */
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
