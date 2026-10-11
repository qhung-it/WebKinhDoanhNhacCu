package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import controller.service.impl.ProductServiceImpl;
import controller.service.intf.ProductService;

@WebServlet(urlPatterns = {"/page", "/brands", "/news", "/news-detail", "/promotions", "/contact", "/info", "/wishlist"})
public class PageController extends HttpServlet {
    private final ProductService productService = new ProductServiceImpl();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String page = request.getServletPath().substring(1);
        if (page.isEmpty() || "page".equals(page)) page = request.getParameter("view");
        if (page == null || !page.matches("(brands|news|news-detail|promotions|contact|info|wishlist)")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if ("promotions".equals(page)) {
            request.setAttribute("products", productService.locSanPham(""));
        }
        ProductController.preparePageData(request);
        request.getRequestDispatcher("/" + page + ".jsp").forward(request, response);
    }
}
