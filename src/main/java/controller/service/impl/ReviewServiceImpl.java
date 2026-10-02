package controller.service.impl;

import controller.service.intf.ReviewService;
import model.dao.impl.ReviewDAOImpl;
import model.dao.intf.ReviewDAO;
import model.entity.Review;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewServiceImpl() {
        this.reviewDAO = new ReviewDAOImpl();
    }

    @Override
    public void guiDanhGia(Review review) {
        reviewDAO.save(review);
    }

    @Override
    public void suaDanhGia(Review review) {
        reviewDAO.update(review);
    }

    @Override
    public void xoaDanhGia(String reviewId) {
        reviewDAO.delete(reviewId);
    }

    @Override
    public void duyetDanhGia(String reviewId) {
        Review review = reviewDAO.findById(reviewId);

        if (review == null) {
            return;
        }

        reviewDAO.update(review);
    }

    @Override
    public List<Review> getDanhGiaTheoSanPham(String productId) {
        return reviewDAO.findByProductId(productId);
    }

    @Override
    public List<Review> getDanhGiaTheoUser(String userId) {
        return reviewDAO.findByUserId(userId);
    }
}