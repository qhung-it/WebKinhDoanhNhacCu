package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.impl.ProductDAOImpl;
import model.dao.intf.ProductDAO;
import model.entity.Brand;
import model.entity.Category;
import model.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        req.setAttribute("products", productDAO.findAll());
        if (id != null && !id.isBlank()) {
            Product product = productDAO.findById(id);
            if (product == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
                return;
            }
            req.setAttribute("editingProduct", product);
        }
        req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("productId"));
        try {
            if ("delete".equals(action)) {
                if (id.isEmpty() || !productDAO.delete(id)) {
                    req.getSession().setAttribute("adminMessage", "Không xóa được sản phẩm. Sản phẩm có thể không tồn tại hoặc đang được sử dụng.");
                } else {
                    req.getSession().setAttribute("adminMessage", "Đã xóa sản phẩm.");
                }
            } else if ("save".equals(action)) {
                Product product = new Product();
                product.setProductId(id);
                product.setProductName(trim(req.getParameter("productName")));
                product.setDescription(trim(req.getParameter("description")));
                product.setImage(trim(req.getParameter("image")));
                product.setPrice(new BigDecimal(trim(req.getParameter("price"))));
                product.setQuantity(Integer.parseInt(trim(req.getParameter("quantity"))));
                String brandId = trim(req.getParameter("brandId"));
                String categoryId = trim(req.getParameter("categoryId"));
                if (!brandId.isEmpty()) {
                    Brand brand = new Brand();
                    brand.setBrandId(brandId);
                    product.setBrand(brand);
                }
                if (!categoryId.isEmpty()) {
                    Category category = new Category();
                    category.setCategoryId(categoryId);
                    product.setCategory(category);
                }

                if (id.isEmpty() || product.getProductName().isEmpty() || product.getPrice().signum() < 0 || product.getQuantity() < 0) {
                    throw new IllegalArgumentException("Mã, tên, giá và số lượng sản phẩm chưa hợp lệ.");
                }
                boolean editing = productDAO.findById(id) != null;
                boolean saved = editing ? productDAO.update(product) : productDAO.save(product);
                req.getSession().setAttribute("adminMessage", saved ? "Đã lưu sản phẩm." : "Không lưu được sản phẩm. Hãy kiểm tra mã và mã thương hiệu/danh mục.");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không hợp lệ");
                return;
            }
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("adminMessage", "Dữ liệu không hợp lệ. Giá phải là số, số lượng phải là số nguyên không âm.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
