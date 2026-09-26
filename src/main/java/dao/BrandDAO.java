package dao;

import model.entity.Brand;

import java.util.List;

/**
 * DAO quản lý Entity Brand.
 * Vai trò chính: CRUD thương hiệu.
 */
public interface BrandDAO extends BaseDAO<Brand, String> {

    /** Tìm thương hiệu theo tên (chính xác). */
    Brand findByBrandName(String brandName);

    /** Tìm các thương hiệu có tên chứa từ khóa (tìm kiếm gần đúng). */
    List<Brand> searchByName(String keyword);

    /** Thống kê: đếm số lượng sản phẩm thuộc một thương hiệu. */
    long countProducts(String brandId);
}
