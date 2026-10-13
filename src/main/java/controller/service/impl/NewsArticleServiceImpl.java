package controller.service.impl;
import controller.service.intf.NewsArticleService;
import model.dao.impl.NewsArticleDAOImpl;
import model.dao.intf.NewsArticleDAO;
import model.entity.NewsArticle;
import java.time.LocalDateTime;
import java.util.List;
public class NewsArticleServiceImpl implements NewsArticleService {
    private final NewsArticleDAO dao;

    public NewsArticleServiceImpl() {
        this.dao = new NewsArticleDAOImpl();
    }

    public void create(NewsArticle x) {
        validate(x);
        if (x.getCreatedAt() == null) {
            x.setCreatedAt(LocalDateTime.now());
        }
        if (x.isPublished() && x.getPublishedAt() == null) {
            x.setPublishedAt(LocalDateTime.now());
            if (!dao.save(x))
                throw new IllegalStateException("Không lưu được bài viết");
        }
    }

    public void update(NewsArticle x) {
        validate(x);
        if (x.isPublished() && x.getPublishedAt() == null)
            x.setPublishedAt(LocalDateTime.now());
        if (!dao.update(x)) {
            throw new IllegalStateException("Không cập nhật được bài viết");
        }
    }

    public void delete(String id) {
        if (id == null || !dao.delete(id)) {
            throw new IllegalArgumentException("Không tìm thấy bài viết");
        }
    }

    public NewsArticle find(String id) {
        return id == null ? null : dao.findById(id);
    }

    public List<NewsArticle> list() {
        return dao.findAll();
    }

    public List<NewsArticle> published() {
        return dao.findPublished();
    }

    private void validate(NewsArticle x) {
        if (x == null || x.getTitle() == null || x.getTitle().isBlank())
            throw new IllegalArgumentException("Tiêu đề bài viết không được để trống"); }
}
