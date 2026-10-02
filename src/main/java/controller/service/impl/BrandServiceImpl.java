package controller.service.impl;

import controller.service.intf.BrandService;
import model.dao.impl.BrandDAOImpl;
import model.dao.intf.BrandDAO;
import model.entity.Brand;

public class BrandServiceImpl implements BrandService {

    private final BrandDAO brandDAO;

    public BrandServiceImpl() {
        this.brandDAO = new BrandDAOImpl();
    }

    @Override
    public void themThuongHieu(Brand brand) {
        brandDAO.save(brand);
    }

    @Override
    public void suaThuongHieu(Brand brand) {
        brandDAO.update(brand);
    }

    @Override
    public void xoaThuongHieu(String brandId) {
        brandDAO.delete(brandId);
    }

    @Override
    public Brand timThuongHieu(String brandId) {
        return brandDAO.findById(brandId);
    }
}