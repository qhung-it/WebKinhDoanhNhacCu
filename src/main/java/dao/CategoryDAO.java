package dao;

import model.entity.Category;

import java.util.List;

/**
 * DAO quản lý Entity Category.
 * Vai trò chính: CRUD danh mục.
 */
public interface CategoryDAO extends BaseDAO<Category, String> {

    /** Tìm danh mục theo tên. */
    Category findByCategoryName(String categoryName);

    /** Lấy danh sách danh mục đang hoạt động (status = true) hoặc tạm ngưng (status = false). */
    List<Category> findByStatus(boolean status);
}
