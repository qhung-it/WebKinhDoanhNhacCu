package controller.User;

import controller.service.impl.BrandServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.Brand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/brands")
public class BrandServlet extends HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();
    private final BrandServiceImpl service = new BrandServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Brand brand : service.getDanhSachThuongHieu()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", brand.getBrandId());
                row.put("name", brand.getBrandName());
                row.put("desc", brand.getDescription());
                result.add(row);
            }
            JSON.toJson(result, response.getWriter());
        } catch (RuntimeException error) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSON.toJson(Map.of("message", "Không thể tải thương hiệu từ database"), response.getWriter());
        }
    }
}
