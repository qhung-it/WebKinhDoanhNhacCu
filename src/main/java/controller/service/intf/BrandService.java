package controller.service.intf;

import model.entity.Brand;

import java.util.List;

public interface BrandService {

    void themThuongHieu(Brand brand);

    void suaThuongHieu(Brand brand);

    void xoaThuongHieu(String brandId);

    Brand timThuongHieu(String brandId);

    List<Brand> getDanhSachThuongHieu();
}