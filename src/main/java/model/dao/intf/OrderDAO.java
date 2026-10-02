package model.dao.intf;

import model.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDAO {

    boolean save(Order order);

    boolean update(Order order);

    boolean delete(String orderId);

    Order findById(String orderId);

    List<Order> findByUserId(String userId);

    List<Order> findByStatusId(String statusId);

    List<Order> findAll();

    boolean updateStatus(String orderId, String statusId);

    BigDecimal thongKeDoanhThuTheoSanPham(String productId);

    BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId);
}