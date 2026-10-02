package model.dao.intf;

import model.entity.Promotion;

import java.util.List;

public interface PromotionDAO {

    boolean save(Promotion promotion);

    boolean update(Promotion promotion);

    boolean delete(String promotionId);

    Promotion findById(String promotionId);

    List<Promotion> findAll();

    List<Promotion> findByProductId(String productId);

    List<Promotion> findByCustomerRankId(String rankId);
}