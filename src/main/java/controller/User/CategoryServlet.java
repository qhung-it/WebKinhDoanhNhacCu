package controller.User;

import controller.service.impl.CategoryServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Category;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();
    private final CategoryServiceImpl service = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Category category : service.getDanhSachDanhMuc()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", category.getCategoryId());
                row.put("key", category.getCategoryId());
                row.put("name", category.getCategoryName());
                row.put("active", category.isStatus());
                result.add(row);
            }
            JSON.toJson(result, response.getWriter());
        } catch (RuntimeException error) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSON.toJson(Map.of("message", "Không thể tải danh mục từ database"), response.getWriter());
        }
    }
}
