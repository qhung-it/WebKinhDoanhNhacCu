package model.dao.intf;

import model.entity.Product;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public interface ProductDAO {

    boolean save(Product product);

    boolean update(Product product);

    boolean delete(String productId);

    Product findById(String productId);

    List<Product> findAll();

    Map<String, Long> findCompletedSalesCounts();

    Map<String, BigDecimal> findActivePromotionPrices();

    List<Product> filter(String keyword);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByBrandId(String brandId);
}
