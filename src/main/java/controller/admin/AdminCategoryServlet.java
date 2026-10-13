package controller.admin;

import com.google.gson.JsonObject;
import controller.service.impl.CategoryServiceImpl;
import controller.service.impl.ProductServiceImpl;
import controller.service.intf.CategoryService;
import controller.service.intf.ProductService;

import controller.util.ApiUtil;
import model.entity.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/api/categories/*")
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryServiceImpl();
    private final ProductService productService   = new ProductServiceImpl();

    private int countProducts(String categoryId) {
        List<?> l = productService.getSanPhamTheoDanhMuc(categoryId);
        return l == null ? 0 : l.size();
    }

    private Map<String, Object> toMap(Category c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getCategoryId());
        m.put("name", c.getCategoryName());
        m.put("active", c.isStatus());
        m.put("productCount", countProducts(c.getCategoryId()));
        return m;
    }

    private String idFromPath(HttpServletRequest req) {
        String p = req.getPathInfo();                 // "/abc" hoặc "/abc/status"
        if (p == null || p.length() < 2) return null;
        return p.substring(1).split("/")[0];
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = categoryService.getDanhSachDanhMuc()
                .stream().map(this::toMap).collect(Collectors.toList());
        ApiUtil.ok(resp, list);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = ApiUtil.str(ApiUtil.body(req), "name");
        if (name == null || name.length() < 2) { ApiUtil.error(resp, 400, "Nhập tên danh mục"); return; }

        Category c = new Category();
        c.setCategoryId(UUID.randomUUID().toString());   // đổi nếu bạn có cách sinh ID riêng
        c.setCategoryName(name);
        c.setStatus(true);
        categoryService.themDanhMuc(c);
        ApiUtil.json(resp, 201, toMap(c));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        Category c = id == null ? null : categoryService.timDanhMuc(id);
        if (c == null) { ApiUtil.error(resp, 404, "Không tìm thấy danh mục"); return; }

        String name = ApiUtil.str(ApiUtil.body(req), "name");
        if (name == null || name.length() < 2) { ApiUtil.error(resp, 400, "Nhập tên danh mục"); return; }

        c.setCategoryName(name);
        categoryService.suaDanhMuc(c);
        ApiUtil.ok(resp, toMap(c));
    }

    // HttpServlet không có doPatch, nên bắt PATCH ở service()
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) { doPatch(req, resp); return; }
        super.service(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || categoryService.timDanhMuc(id) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy danh mục"); return;
        }
        JsonObject b = ApiUtil.body(req);
        boolean active = b.has("active") && b.get("active").getAsBoolean();
        categoryService.doiTrangThaiDanhMuc(id, active);
        ApiUtil.ok(resp, Map.of("ok", true));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = idFromPath(req);
        if (id == null || categoryService.timDanhMuc(id) == null) {
            ApiUtil.error(resp, 404, "Không tìm thấy danh mục"); return;
        }
        int n = countProducts(id);
        if (n > 0) { ApiUtil.error(resp, 409, "Không thể xóa: còn " + n + " sản phẩm thuộc danh mục này"); return; }

        categoryService.xoaDanhMuc(id);
        ApiUtil.ok(resp, Map.of("ok", true));
    }
}