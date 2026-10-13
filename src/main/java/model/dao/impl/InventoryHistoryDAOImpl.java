package model.dao.impl;
import jakarta.persistence.EntityManager;
import model.dao.intf.InventoryHistoryDAO;
import model.entity.InventoryHistory;
import model.entity.Product;
import model.util.JpaUtil;
import java.util.List;
public class InventoryHistoryDAOImpl implements InventoryHistoryDAO {
    public boolean save(InventoryHistory value) { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { em.getTransaction().begin(); value.setProduct(em.getReference(Product.class, value.getProduct().getProductId())); em.persist(value); em.getTransaction().commit(); return true; } catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; } finally { em.close(); } }
    public List<InventoryHistory> findAll() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.createQuery("SELECT h FROM InventoryHistory h JOIN FETCH h.product ORDER BY h.changedAt DESC", InventoryHistory.class).getResultList(); } finally { em.close(); } }
}
