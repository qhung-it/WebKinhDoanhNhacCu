package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.ContactMessageDAO;
import model.entity.ContactMessage;
import model.util.JpaUtil;
import java.util.List;

public class ContactMessageDAOImpl implements ContactMessageDAO {
    public boolean save(ContactMessage value) { return persist(value); }
    public boolean update(ContactMessage value) { return persist(value); }
    private boolean persist(ContactMessage value) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try { em.getTransaction().begin(); em.merge(value); em.getTransaction().commit(); return true; }
        catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; }
        finally { em.close(); }
    }
    public boolean delete(String id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try { em.getTransaction().begin(); ContactMessage value = em.find(ContactMessage.class, id); if (value == null) { em.getTransaction().rollback(); return false; } em.remove(value); em.getTransaction().commit(); return true; }
        catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; }
        finally { em.close(); }
    }
    public ContactMessage findById(String id) { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.find(ContactMessage.class, id); } finally { em.close(); } }
    public List<ContactMessage> findAll() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.createQuery("SELECT c FROM ContactMessage c ORDER BY c.createdAt DESC", ContactMessage.class).getResultList(); } finally { em.close(); } }
}
