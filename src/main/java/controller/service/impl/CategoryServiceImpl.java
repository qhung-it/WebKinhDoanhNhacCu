package controller.service.impl;

import controller.service.intf.CategoryService;
import model.dao.impl.CategoryDAOImpl;
import model.dao.intf.CategoryDAO;
import model.entity.Category;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    @Override
    public void themDanhMuc(Category category) {
        categoryDAO.save(category);
    }

    @Override
    public void suaDanhMuc(Category category) {
        categoryDAO.update(category);
    }

    @Override
    public void xoaDanhMuc(String categoryId) {
        categoryDAO.delete(categoryId);
    }

    @Override
    public Category timDanhMuc(String categoryId) {
        return categoryDAO.findById(categoryId);
    }

    @Override
    public List<Category> getDanhSachDanhMuc() {
        return categoryDAO.findAll();
    }

    @Override
    public void doiTrangThaiDanhMuc(String categoryId, boolean status) {
        categoryDAO.updateStatus(categoryId, status);
    }
}