package controller.service.impl;

import controller.service.intf.PromotionService;
import model.dao.impl.PromotionDAOImpl;
import model.dao.intf.PromotionDAO;
import model.entity.Promotion;

import java.util.List;

public class PromotionServiceImpl implements PromotionService {

    private final PromotionDAO promotionDAO;

    public PromotionServiceImpl() {
        this.promotionDAO = new PromotionDAOImpl();
    }

    @Override
    public void themKhuyenMai(Promotion promotion) {
        if (!promotionDAO.save(promotion)) {
            throw new IllegalStateException("Không lưu được khuyến mãi vào database");
        }
    }

    @Override
    public void suaKhuyenMai(Promotion promotion) {
        if (!promotionDAO.update(promotion)) {
            throw new IllegalStateException("Không cập nhật được khuyến mãi trong database");
        }
    }

    @Override
    public void xoaKhuyenMai(String promotionId) {
        if (!promotionDAO.delete(promotionId)) {
            throw new IllegalStateException("Không xóa được khuyến mãi khỏi database");
        }
    }

    @Override
    public Promotion timKhuyenMai(String promotionId) {
        return promotionDAO.findById(promotionId);
    }

    @Override
    public List<Promotion> getDanhSachKhuyenMai() {
        return promotionDAO.findAll();
    }

    @Override
    public List<Promotion> getKhuyenMaiTheoSanPham(String productId) {
        return promotionDAO.findByProductId(productId);
    }

    @Override
    public List<Promotion> getKhuyenMaiTheoHang(String rankId) {
        return promotionDAO.findByCustomerRankId(rankId);
    }
}
