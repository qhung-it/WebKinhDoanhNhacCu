package controller.service.intf;

import model.entity.Brand;

public interface BrandService {

    void themThuongHieu(Brand brand);

    void suaThuongHieu(Brand brand);

    void xoaThuongHieu(String brandId);

    Brand timThuongHieu(String brandId);
}