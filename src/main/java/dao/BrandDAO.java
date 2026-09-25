package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Brand;

import java.util.List;

/**
 * DAO quản lý Entity Brand.
 * Vai trò chính: CRUD thương hiệu.
 */
public class BrandDAO extends BaseDAO<Brand, String> {

    public BrandDAO() {
        super(Brand.class);
    }

    /** Tìm thương hiệu theo tên (chính xác). */
    public Brand findByBrandName(String brandName) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Brand> query = em.createQuery(
                    "SELECT b FROM Brand b WHERE b.brandName = :brandName", Brand.class);
            query.setParameter("brandName", brandName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /** Tìm các thương hiệu có tên chứa từ khóa (tìm kiếm gần đúng). */
    public List<Brand> searchByName(String keyword) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Brand> query = em.createQuery(
                    "SELECT b FROM Brand b WHERE LOWER(b.brandName) LIKE LOWER(:keyword)", Brand.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
