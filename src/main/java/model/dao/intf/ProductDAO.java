package model.dao.intf;

import model.entity.Product;

import java.util.List;

public interface ProductDAO {

    boolean save(Product product);

    boolean update(Product product);

    boolean delete(String productId);

    Product findById(String productId);

    List<Product> findAll();

    List<Product> filter(String keyword);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByBrandId(String brandId);
}