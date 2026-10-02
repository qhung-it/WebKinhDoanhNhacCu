package model.dao.intf;

import model.entity.Review;

import java.util.List;

public interface ReviewDAO {

    boolean save(Review review);

    boolean update(Review review);

    boolean delete(String reviewId);

    Review findById(String reviewId);

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    List<Review> findAll();
}