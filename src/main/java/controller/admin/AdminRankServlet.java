package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.CustomerRankServiceImpl;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.CustomerRankService;
import controller.service.intf.UserService;
import controller.util.ApiUtil;
import model.RankName;
import model.entity.CustomerRank;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/admin/api/ranks/*")
public class AdminRankServlet extends HttpServlet {

    private final CustomerRankService rankService = new CustomerRankServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> ranks = rankService.getDanhSachHang().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        ApiUtil.ok(resp, ranks);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CustomerRank rank = new CustomerRank();
            rank.setRankId(UUID.randomUUID().toString());
            apply(rank, ApiUtil.body(req));

            if (rankService.getDanhSachHang().stream().anyMatch(r -> r.getRankName() == rank.getRankName())) {
                ApiUtil.error(resp, 409, "Hạng thành viên đã tồn tại");
                return;
            }

            rankService.themHang(rank);
            ApiUtil.json(resp, 201, toMap(rank));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        CustomerRank rank = (id == null) ? null : rankService.timHang(id);

        if (rank == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy hạng thành viên");
            return;
        }

        try {
            apply(rank, ApiUtil.body(req));
            rankService.suaHang(rank);
            ApiUtil.ok(resp, toMap(rank));
        } catch (IllegalArgumentException e) {
            ApiUtil.error(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        CustomerRank rank = (id == null) ? null : rankService.timHang(id);

        if (rank == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy hạng thành viên");
            return;
        }

        if (userService.getDanhSachUser().stream().anyMatch(u -> u.getCustomerRank() != null
                && id.equals(u.getCustomerRank().getRankId()))) {
            ApiUtil.error(resp, 409, "Không thể xóa hạng đang được khách hàng sử dụng");
            return;
        }

        rankService.xoaHang(id);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    private String idFromPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        return (path == null || path.length() < 2) ? null : path.substring(1).split("/")[0];
    }

    private void apply(CustomerRank rank, JsonObject body) {
        try {
            rank.setRankName(RankName.valueOf(ApiUtil.str(body, "name")));
            BigDecimal minimum = body.get("minSpending").getAsBigDecimal();

            if (minimum.signum() < 0) {
                throw new IllegalArgumentException("Mức chi tiêu không được âm");
            }
            rank.setMinSpending(minimum);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tên hạng hoặc mức chi tiêu không hợp lệ");
        } catch (Exception e) {
            throw new IllegalArgumentException("Nhập mức chi tiêu hợp lệ");
        }
    }

    private Map<String, Object> toMap(CustomerRank rank) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", rank.getRankId());
        map.put("name", rank.getRankName() == null ? null : rank.getRankName().name());
        map.put("minSpending", rank.getMinSpending());
        return map;
    }
}