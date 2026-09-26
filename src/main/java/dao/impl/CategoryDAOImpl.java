package dao.impl;

import dao.CategoryDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Category;

import java.util.List;

/**
 * Triển khai (implement) CategoryDAO.
 */
public class CategoryDAOImpl extends BaseDAOImpl<Category, String> implements CategoryDAO {

    public CategoryDAOImpl() {
        super(Category.class);
    }

    @Override
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

    @Override
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
