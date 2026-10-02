package model.dao.intf;

import model.entity.Brand;

import java.util.List;

public interface BrandDAO {

    boolean save(Brand brand);

    boolean update(Brand brand);

    boolean delete(String brandId);

    Brand findById(String brandId);

    List<Brand> findAll();
}