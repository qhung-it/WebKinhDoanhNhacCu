package controller.service.intf;

import model.entity.Category;

import java.util.List;

public interface CategoryService {

    void themDanhMuc(Category category);

    void suaDanhMuc(Category category);

    void xoaDanhMuc(String categoryId);

    Category timDanhMuc(String categoryId);

    List<Category> getDanhSachDanhMuc();

    void doiTrangThaiDanhMuc(String categoryId, boolean status);
}