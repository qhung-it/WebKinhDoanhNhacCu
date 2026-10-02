package controller.service.intf;

import model.entity.Product;

import java.util.List;

public interface ProductService {

    void themSanPham(Product product);

    void suaSanPham(Product product);

    void xoaSanPham(String productId);

    Product timSanPham(String productId);

    List<Product> locSanPham(String keyword);

    List<Product> getSanPhamTheoDanhMuc(String categoryId);

    List<Product> getSanPhamTheoThuongHieu(String brandId);
}