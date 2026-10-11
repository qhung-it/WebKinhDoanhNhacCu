package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import controller.service.impl.ProductServiceImpl;
import controller.service.intf.ProductService;

@WebServlet("/home")
public class HomeController extends HttpServlet {
    private final ProductService productService = new ProductServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("products", productService.locSanPham(""));
        ProductController.preparePageData(req);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
