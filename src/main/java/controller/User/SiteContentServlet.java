package controller.User;

import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.util.JpaUtil;
import java.io.IOException;

@WebServlet("/site-content")
public class SiteContentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager()) {
            String payload = (String) entityManager.createNativeQuery(
                    "SELECT json_data FROM dbo.site_content WHERE content_key = :contentKey")
                    .setParameter("contentKey", "soundora")
                    .getSingleResult();
            com.google.gson.JsonParser.parseString(payload);
            response.getWriter().write(payload);
        } catch (jakarta.persistence.NoResultException error) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\":\"Chưa có dữ liệu giao diện trong database\"}");
        } catch (RuntimeException error) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"Không thể tải dữ liệu giao diện từ database\"}");
        }
    }
}
