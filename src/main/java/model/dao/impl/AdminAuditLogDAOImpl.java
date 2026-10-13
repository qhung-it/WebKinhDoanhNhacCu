package model.dao.impl;
import jakarta.persistence.EntityManager;
import model.dao.intf.AdminAuditLogDAO;
import model.entity.AdminAuditLog;
import model.util.JpaUtil;
import java.util.List;
public class AdminAuditLogDAOImpl implements AdminAuditLogDAO {
    public boolean save(AdminAuditLog value) { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { em.getTransaction().begin(); em.persist(value); em.getTransaction().commit(); return true; } catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; } finally { em.close(); } }
    public List<AdminAuditLog> findAll() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.createQuery("SELECT a FROM AdminAuditLog a ORDER BY a.createdAt DESC", AdminAuditLog.class).setMaxResults(500).getResultList(); } finally { em.close(); } }
}
