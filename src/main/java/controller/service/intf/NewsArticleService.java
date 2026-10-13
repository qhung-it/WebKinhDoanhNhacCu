package controller.service.intf;
import model.entity.NewsArticle;
import java.util.List;
public interface NewsArticleService { void create(NewsArticle value); void update(NewsArticle value); void delete(String id); NewsArticle find(String id); List<NewsArticle> list(); List<NewsArticle> published(); }
