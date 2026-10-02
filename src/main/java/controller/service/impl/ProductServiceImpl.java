package controller.service.impl;

import controller.service.intf.ProductService;
import model.dao.impl.ProductDAOImpl;
import model.dao.intf.ProductDAO;
import model.entity.Product;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    public ProductServiceImpl() {
        this.productDAO = new ProductDAOImpl();
    }

    @Override
    public void themSanPham(Product product) {
        productDAO.save(product);
    }

    @Override
    public void suaSanPham(Product product) {
        productDAO.update(product);
    }

    @Override
    public void xoaSanPham(String productId) {
        productDAO.delete(productId);
    }

    @Override
    public Product timSanPham(String productId) {
        return productDAO.findById(productId);
    }

    @Override
    public List<Product> locSanPham(String keyword) {
        return productDAO.filter(keyword);
    }

    @Override
    public List<Product> getSanPhamTheoDanhMuc(String categoryId) {
        return productDAO.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getSanPhamTheoThuongHieu(String brandId) {
        return productDAO.findByBrandId(brandId);
    }
}