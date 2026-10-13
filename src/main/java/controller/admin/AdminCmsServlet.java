package controller.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.service.impl.AdminAuditLogServiceImpl;
import controller.service.impl.ContactMessageServiceImpl;
import controller.service.impl.InventoryHistoryServiceImpl;
import controller.service.impl.NewsArticleServiceImpl;
import controller.service.impl.SiteSettingServiceImpl;
import controller.service.intf.AdminAuditLogService;
import controller.service.intf.ContactMessageService;
import controller.service.intf.InventoryHistoryService;
import controller.service.intf.NewsArticleService;
import controller.service.intf.SiteSettingService;
import controller.util.ApiUtil;
import model.entity.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/api/cms/*")
public class AdminCmsServlet extends HttpServlet {

    private final ContactMessageService contacts = new ContactMessageServiceImpl();
    private final NewsArticleService news = new NewsArticleServiceImpl();
    private final SiteSettingService settings = new SiteSettingServiceImpl();
    private final InventoryHistoryService inventory = new InventoryHistoryServiceImpl();
    private final AdminAuditLogService audit = new AdminAuditLogServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);

        if (p.equals("/contacts")) {
            ApiUtil.ok(resp, contacts.list().stream().map(this::contactMap).collect(Collectors.toList()));
        } else if (p.equals("/news")) {
            ApiUtil.ok(resp, news.list().stream().map(this::newsMap).collect(Collectors.toList()));
        } else if (p.equals("/settings")) {
            ApiUtil.ok(resp, settingMap(settings.get()));
        } else if (p.equals("/inventory-history")) {
            ApiUtil.ok(resp, inventory.list().stream().map(this::inventoryMap).collect(Collectors.toList()));
        } else if (p.equals("/audit")) {
            ApiUtil.ok(resp, audit.list().stream().map(this::auditMap).collect(Collectors.toList()));
        } else {
            ApiUtil.error(resp, 404, "Không tìm thấy API");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);
        JsonObject b = ApiUtil.body(req);

        try {
            if (p.equals("/news")) {
                NewsArticle x = readNews(b, null);
                news.create(x);
                ApiUtil.json(resp, 201, newsMap(x));
            } else if (p.equals("/audit")) {
                AdminAuditLog x = new AdminAuditLog();
                x.setActorEmail(actor(req));
                x.setAction(ApiUtil.str(b, "action"));
                x.setDetail(ApiUtil.str(b, "detail"));
                audit.record(x);
                ApiUtil.json(resp, 201, auditMap(x));
            } else {
                ApiUtil.error(resp, 404, "Không tìm thấy API");
            }
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        } catch (IllegalStateException e) {
            ApiUtil.error(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);
        JsonObject b = ApiUtil.body(req);

        try {
            if (p.equals("/settings")) {
                SiteSetting x = settings.get();
                x.setFreeShippingFrom(b.get("freeShipFrom").getAsBigDecimal());
                x.setShippingFee(b.get("shipFee").getAsBigDecimal());
                settings.save(x);
                ApiUtil.ok(resp, settingMap(settings.get()));
            } else if (p.startsWith("/news/")) {
                NewsArticle x = news.find(id(req));
                if (x == null) {
                    ApiUtil.error(resp, 404, "Không tìm thấy bài viết");
                    return;
                }
                readNews(b, x);
                news.update(x);
                ApiUtil.ok(resp, newsMap(x));
            } else {
                ApiUtil.error(resp, 404, "Không tìm thấy API");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        } catch (Exception e) {
            ApiUtil.error(resp, 400, "Dữ liệu cấu hình không hợp lệ");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);
        String targetId = id(req);
        JsonObject b = ApiUtil.body(req);

        try {
            if (p.startsWith("/contacts/")) {
                ContactMessage x = contacts.find(targetId);
                if (x == null) {
                    ApiUtil.error(resp, 404, "Không tìm thấy liên hệ");
                    return;
                }
                String status = ApiUtil.str(b, "status");
                if (status != null && !List.of("Mới", "Đang xử lý", "Đã xử lý").contains(status)) {
                    throw new IllegalArgumentException("Trạng thái liên hệ không hợp lệ");
                }
                if (status != null) {
                    x.setStatus(status);
                }
                if (b.has("note")) {
                    x.setNote(ApiUtil.str(b, "note"));
                }
                contacts.update(x);
                ApiUtil.ok(resp, contactMap(x));
            } else {
                ApiUtil.error(resp, 404, "Không tìm thấy API");
            }
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);
        String targetId = id(req);

        try {
            if (p.startsWith("/contacts/")) {
                contacts.delete(targetId);
            } else if (p.startsWith("/news/")) {
                news.delete(targetId);
            } else {
                ApiUtil.error(resp, 404, "Không tìm thấy API");
                return;
            }
            ApiUtil.ok(resp, Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 404, e.getMessage());
        }
    }

    private String path(HttpServletRequest req) {
        return req.getPathInfo() == null ? "/" : req.getPathInfo();
    }

    private String id(HttpServletRequest req) {
        String[] parts = path(req).split("/");
        return parts.length > 2 ? parts[2] : null;
    }

    private String actor(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("adminUser");
        if (value instanceof Map) {
            return String.valueOf(((Map<?, ?>) value).get("email"));
        }
        return null;
    }

    private NewsArticle readNews(JsonObject b, NewsArticle x) {
        if (x == null) {
            x = new NewsArticle();
        }
        x.setTitle(ApiUtil.str(b, "title"));
        x.setCategory(ApiUtil.str(b, "cat"));
        x.setExcerpt(ApiUtil.str(b, "excerpt"));

        JsonElement content = b.get("content");
        if (content != null && content.isJsonArray()) {
            JsonArray arr = content.getAsJsonArray();
            List<String> paragraphs = new ArrayList<>();
            arr.forEach(e -> paragraphs.add(e.getAsString()));
            x.setContent(String.join("\n\n", paragraphs));
        } else {
            x.setContent(ApiUtil.str(b, "content"));
        }

        x.setEmoji(ApiUtil.str(b, "emoji"));
        x.setAuthor(ApiUtil.str(b, "author"));
        x.setColor(ApiUtil.str(b, "color"));
        x.setColor2(ApiUtil.str(b, "color2"));
        x.setPublished(!b.has("published") || b.get("published").getAsBoolean());
        return x;
    }

    private Map<String, Object> contactMap(ContactMessage x) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", x.getContactId());
        m.put("name", x.getName());
        m.put("email", x.getEmail());
        m.put("phone", x.getPhone());
        m.put("subject", x.getSubject());
        m.put("msg", x.getMessage());
        m.put("status", x.getStatus());
        m.put("note", x.getNote());
        m.put("date", x.getCreatedAt());
        return m;
    }

    private Map<String, Object> newsMap(NewsArticle x) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", x.getArticleId());
        m.put("title", x.getTitle());
        m.put("cat", x.getCategory());
        m.put("excerpt", x.getExcerpt());
        m.put("content", x.getContent() == null || x.getContent().isBlank()
                ? List.of()
                : Arrays.asList(x.getContent().split("\\R\\s*\\R")));
        m.put("emoji", x.getEmoji());
        m.put("author", x.getAuthor());
        m.put("color", x.getColor());
        m.put("color2", x.getColor2());
        m.put("published", x.isPublished());
        m.put("date", x.getPublishedAt() == null ? x.getCreatedAt() : x.getPublishedAt());
        return m;
    }

    private Map<String, Object> settingMap(SiteSetting x) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("freeShipFrom", x.getFreeShippingFrom());
        m.put("shipFee", x.getShippingFee());
        return m;
    }

    private Map<String, Object> inventoryMap(InventoryHistory x) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", x.getInventoryHistoryId());
        m.put("productId", x.getProduct().getProductId());
        m.put("name", x.getProduct().getProductName());
        m.put("sku", x.getProduct().getSku());
        m.put("type", x.getOperation());
        m.put("qty", x.getQuantityChange());
        m.put("before", x.getStockBefore());
        m.put("after", x.getStockAfter());
        m.put("note", x.getNote());
        m.put("by", x.getChangedBy());
        m.put("at", x.getChangedAt());
        return m;
    }

    private Map<String, Object> auditMap(AdminAuditLog x) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", x.getAuditId());
        m.put("by", x.getActorEmail());
        m.put("action", x.getAction());
        m.put("detail", x.getDetail());
        m.put("at", x.getCreatedAt());
        return m;
    }
}