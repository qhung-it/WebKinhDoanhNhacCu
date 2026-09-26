package dao;

import model.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO quản lý Entity Product.
 * Vai trò chính: CRUD sản phẩm, tìm kiếm/lọc, tồn kho.
 */
public interface ProductDAO extends BaseDAO<Product, String> {

    /** Tìm kiếm sản phẩm theo tên (gần đúng, không phân biệt hoa thường). */
    List<Product> searchByName(String keyword);

    /** Lọc sản phẩm theo danh mục. */
    List<Product> findByCategory(String categoryId);

    /** Lọc sản phẩm theo thương hiệu. */
    List<Product> findByBrand(String brandId);

    /** Lọc sản phẩm theo khoảng giá [minPrice, maxPrice]. */
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /** Lọc sản phẩm theo trạng thái (ví dụ: "ACTIVE", "HIDDEN", ...). */
    List<Product> findByStatus(String status);

    /** Lấy danh sách sản phẩm còn hàng (quantity > 0). */
    List<Product> findInStock();

    /** Lấy danh sách sản phẩm hết hàng (quantity <= 0). */
    List<Product> findOutOfStock();

    /** Kiểm tra số lượng tồn kho hiện tại của một sản phẩm. */
    int getStockQuantity(String productId);

    /**
     * Cập nhật (tăng/giảm) số lượng tồn kho của sản phẩm.
     * delta âm để giảm (khi bán hàng), dương để tăng (khi nhập hàng/hủy đơn).
     * Trả về true nếu cập nhật thành công, false nếu không đủ tồn kho hoặc không tìm thấy sản phẩm.
     */
    boolean adjustStock(String productId, int delta);

    /** Thống kê: tổng số sản phẩm hiện có trong hệ thống. */
    long countAll();

    /** Thống kê: đếm số lượng sản phẩm thuộc một danh mục. */
    long countByCategory(String categoryId);

    /** Thống kê: đếm số lượng sản phẩm thuộc một thương hiệu. */
    long countByBrand(String brandId);

    /** Thống kê: đếm số lượng sản phẩm theo trạng thái (ví dụ: "ACTIVE", "HIDDEN", ...). */
    long countByStatus(String status);
}
