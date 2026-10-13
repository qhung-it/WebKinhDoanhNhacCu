package model.dao.impl;
import jakarta.persistence.EntityManager;
import model.dao.intf.SiteSettingDAO;
import model.entity.SiteSetting;
import model.util.JpaUtil;
public class SiteSettingDAOImpl implements SiteSettingDAO {
    public SiteSetting findDefault() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.find(SiteSetting.class, "default"); } finally { em.close(); } }
    public boolean saveOrUpdate(SiteSetting value) { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { em.getTransaction().begin(); em.merge(value); em.getTransaction().commit(); return true; } catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; } finally { em.close(); } }
}
