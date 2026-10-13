package controller.service.intf;

import model.entity.Review;

import java.util.List;

public interface ReviewService {

    void guiDanhGia(Review review);

    void suaDanhGia(Review review);

    void xoaDanhGia(String reviewId);

    void duyetDanhGia(String reviewId);

    void capNhatDuyetDanhGia(String reviewId, boolean approved);

    List<Review> getDanhSachDanhGia();

    List<Review> getDanhGiaTheoSanPham(String productId);

    List<Review> getDanhGiaTheoUser(String userId);
}
