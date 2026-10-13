package controller.admin;

import controller.service.impl.CustomerRankServiceImpl;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.CustomerRankService;
import controller.service.intf.UserService;
import controller.util.ApiUtil;
import model.entity.CustomerRank;
import model.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/admin/api/customers/*")
public class AdminCustomerServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();
    private final CustomerRankService rankService = new CustomerRankServiceImpl();

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
        List<Map<String, Object>> users = userService.getDanhSachUser().stream()
                .filter(u -> u.getRole() == null || u.getRole().name().equals("USER"))
                .map(this::toMap)
                .collect(Collectors.toList());

        ApiUtil.ok(resp, users);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        User user = (id == null) ? null : userService.timUser(id);

        if (user == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy khách hàng");
            return;
        }

        String rankId = ApiUtil.str(ApiUtil.body(req), "rankId");
        if (rankId == null || rankId.isBlank()) {
            ApiUtil.error(resp, 400, "Thiếu mã hạng thành viên");
            return;
        }

        if (rankService.timHang(rankId) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy hạng thành viên");
            return;
        }

        userService.capNhatHangKhachHang(id, rankId);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    private String idFromPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        return (path == null || path.length() < 2) ? null : path.substring(1).split("/")[0];
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        CustomerRank rank = user.getCustomerRank();

        map.put("id", user.getUserId());
        map.put("name", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("address", user.getAddress());
        map.put("role", user.getRole() == null ? null : user.getRole().name());
        map.put("rankId", rank == null ? null : rank.getRankId());
        map.put("rank", (rank == null || rank.getRankName() == null) ? null : rank.getRankName().name());

        return map;
    }
}