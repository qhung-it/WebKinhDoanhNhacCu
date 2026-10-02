package controller.service.intf;

import model.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    void taoDonHang(Order order);

    void huyDonHang(String orderId);

    Order timDonHang(String orderId);

    List<Order> getDonHangTheoUser(String userId);

    List<Order> getDonHangTheoTrangThai(String statusId);

    void capNhatTrangThai(String orderId, String statusId);

    BigDecimal thongKeDoanhThuTheoSanPham(String productId);

    BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId);
}