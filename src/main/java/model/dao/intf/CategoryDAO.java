package model.dao.intf;

import model.entity.Category;

import java.util.List;

public interface CategoryDAO {

    boolean save(Category category);

    boolean update(Category category);

    boolean delete(String categoryId);

    Category findById(String categoryId);

    List<Category> findAll();

    boolean updateStatus(String categoryId, boolean status);
}