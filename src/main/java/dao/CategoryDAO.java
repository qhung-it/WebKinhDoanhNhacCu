package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Category;

import java.util.List;

/**
 * DAO quản lý Entity Category.
 * Vai trò chính: CRUD danh mục.
 */
public class CategoryDAO extends BaseDAO<Category, String> {

    public CategoryDAO() {
        super(Category.class);
    }

    /** Tìm danh mục theo tên. */
    public Category findByCategoryName(String categoryName) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                    "SELECT c FROM Category c WHERE c.categoryName = :categoryName", Category.class);
            query.setParameter("categoryName", categoryName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách danh mục đang hoạt động (status = true) hoặc tạm ngưng (status = false). */
    public List<Category> findByStatus(boolean status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                    "SELECT c FROM Category c WHERE c.status = :status", Category.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
