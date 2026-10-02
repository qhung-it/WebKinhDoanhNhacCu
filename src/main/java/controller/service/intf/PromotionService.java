package controller.service.intf;

import model.entity.Promotion;

import java.util.List;

public interface PromotionService {

    void themKhuyenMai(Promotion promotion);

    void suaKhuyenMai(Promotion promotion);

    void xoaKhuyenMai(String promotionId);

    Promotion timKhuyenMai(String promotionId);

    List<Promotion> getKhuyenMaiTheoSanPham(String productId);

    List<Promotion> getKhuyenMaiTheoHang(String rankId);
}