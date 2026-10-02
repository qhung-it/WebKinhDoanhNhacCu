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
        promotionDAO.save(promotion);
    }

    @Override
    public void suaKhuyenMai(Promotion promotion) {
        promotionDAO.update(promotion);
    }

    @Override
    public void xoaKhuyenMai(String promotionId) {
        promotionDAO.delete(promotionId);
    }

    @Override
    public Promotion timKhuyenMai(String promotionId) {
        return promotionDAO.findById(promotionId);
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