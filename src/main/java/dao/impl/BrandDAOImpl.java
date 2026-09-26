package dao.impl;

import dao.BrandDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.entity.Brand;

import java.util.List;

/**
 * Triển khai (implement) BrandDAO.
 */
public class BrandDAOImpl extends BaseDAOImpl<Brand, String> implements BrandDAO {

    public BrandDAOImpl() {
        super(Brand.class);
    }

    @Override
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

    @Override
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
