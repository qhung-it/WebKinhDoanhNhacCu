package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.NewsArticleDAO;
import model.entity.NewsArticle;
import model.util.JpaUtil;
import java.util.List;

public class NewsArticleDAOImpl implements NewsArticleDAO {
    public boolean save(NewsArticle value) { return persist(value); }
    public boolean update(NewsArticle value) { return persist(value); }
    private boolean persist(NewsArticle value) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try { em.getTransaction().begin(); em.merge(value); em.getTransaction().commit(); return true; }
        catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; }
        finally { em.close(); }
    }
    public boolean delete(String id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try { em.getTransaction().begin(); NewsArticle value = em.find(NewsArticle.class, id); if (value == null) { em.getTransaction().rollback(); return false; } em.remove(value); em.getTransaction().commit(); return true; }
        catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); return false; }
        finally { em.close(); }
    }
    public NewsArticle findById(String id) { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.find(NewsArticle.class, id); } finally { em.close(); } }
    public List<NewsArticle> findAll() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.createQuery("SELECT n FROM NewsArticle n ORDER BY n.createdAt DESC", NewsArticle.class).getResultList(); } finally { em.close(); } }
    public List<NewsArticle> findPublished() { EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager(); try { return em.createQuery("SELECT n FROM NewsArticle n WHERE n.published = true ORDER BY n.publishedAt DESC", NewsArticle.class).getResultList(); } finally { em.close(); } }
}
