package model.dao.intf;

import model.OrderStatus;
import model.entity.Order;
import model.entity.Payment;
import model.entity.Deposit;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDAO {

    boolean save(Order order);

    boolean save(Order order, Payment payment, Deposit deposit);

    boolean update(Order order);

    boolean delete(String orderId);

    Order findById(String orderId);

    List<Order> findByUserId(String userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();

    BigDecimal thongKeDoanhThuTheoSanPham(String productId);

    BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId);
}