package model.dao.intf;

import model.entity.NewsArticle;
import java.util.List;

public interface NewsArticleDAO {
    boolean save(NewsArticle value);
    boolean update(NewsArticle value);
    boolean delete(String id);
    NewsArticle findById(String id);
    List<NewsArticle> findAll();
    List<NewsArticle> findPublished();
}
