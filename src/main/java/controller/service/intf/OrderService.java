package controller.service.intf;

import model.OrderStatus;
import model.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    void taoDonHang(Order order);

    void huyDonHang(String orderId);

    Order timDonHang(String orderId);

    List<Order> getDonHangTheoUser(String userId);

    List<Order> getDonHangTheoTrangThai(OrderStatus status);

    void capNhatTrangThai(String orderId, OrderStatus status);

    BigDecimal thongKeDoanhThuTheoSanPham(String productId);

    BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId);
}