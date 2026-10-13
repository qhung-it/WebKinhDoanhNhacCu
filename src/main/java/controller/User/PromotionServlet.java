package controller.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.impl.PromotionDAOImpl;
import model.entity.Promotion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/promotions")
public class PromotionServlet extends HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();
    private final PromotionDAOImpl promotions = new PromotionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Promotion promotion : promotions.findAll()) {
            if (promotion.getCouponCode() != null && promotion.isActive()) result.add(toMap(promotion));
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSON.toJson(result, response.getWriter());
    }

    private Map<String, Object> toMap(Promotion promotion) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("code", promotion.getCouponCode());
        row.put("name", promotion.getPromotionName());
        row.put("type", promotion.isFreeShipping() ? "ship"
                : promotion.getDiscountType() == model.DiscountType.PERCENTAGE ? "percent" : "amount");
        row.put("value", promotion.isFreeShipping() || promotion.getDiscountValue() == null
                ? 0 : promotion.getDiscountValue());
        row.put("max", promotion.getMaxDiscount());
        row.put("min", promotion.getMinimumOrderAmount());
        row.put("desc", promotion.getDescription() == null ? promotion.getPromotionName() : promotion.getDescription());
        row.put("active", promotion.isActive());
        row.put("start", promotion.getStartDateTime() == null ? null : promotion.getStartDateTime().toLocalDate().toString());
        row.put("end", promotion.getEndDateTime() == null ? null : promotion.getEndDateTime().toLocalDate().toString());
        return row;
    }
}
