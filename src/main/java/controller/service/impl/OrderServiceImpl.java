package controller.service.impl;

import controller.service.intf.OrderService;
import model.OrderStatus;
import model.dao.impl.OrderDAOImpl;
import model.dao.intf.OrderDAO;
import model.entity.Order;
import model.entity.OrderLineItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;

    public OrderServiceImpl() {
        this.orderDAO = new OrderDAOImpl();
    }

    @Override
    public void taoDonHang(Order order) {

        if (order == null) {
            return;
        }

        if (order.getOrderLineItems() == null ||
                order.getOrderLineItems().isEmpty()) {
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderLineItem item : order.getOrderLineItems()) {

            if (item == null ||
                    item.getProduct() == null ||
                    item.getQuantity() <= 0) {
                continue;
            }

            item.setOrder(order);

            BigDecimal thanhTien = item.calculateSubtotal();

            subtotal = subtotal.add(thanhTien);
        }

        order.setSubtotal(subtotal);

        BigDecimal discount = order.getDiscount();

        if (discount == null ||
                discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        order.setDiscount(discount);

        BigDecimal totalAmount = subtotal.subtract(discount);

        order.setTotalAmount(totalAmount);

        orderDAO.save(order);
    }

    @Override
    public void huyDonHang(String orderId) {

        if (orderId == null || orderId.trim().isEmpty()) {
            return;
        }

        Order order = orderDAO.findById(orderId);

        if (order == null) {
            return;
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderDAO.update(order);
    }

    @Override
    public Order timDonHang(String orderId) {
        return orderDAO.findById(orderId);
    }

    @Override
    public List<Order> getDonHangTheoUser(String userId) {
        return orderDAO.findByUserId(userId);
    }

    @Override
    public List<Order> getDonHangTheoTrangThai(OrderStatus status) {

        if (status == null) {
            return new ArrayList<>();
        }

        return orderDAO.findByStatus(status);
    }

    @Override
    public void capNhatTrangThai(String orderId, OrderStatus status) {

        if (orderId == null ||
                orderId.trim().isEmpty() ||
                status == null) {
            return;
        }

        Order order = orderDAO.findById(orderId);

        if (order == null) {
            return;
        }

        order.setOrderStatus(status);

        orderDAO.update(order);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoSanPham(String productId) {

        if (productId == null || productId.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderDAO.thongKeDoanhThuTheoSanPham(productId);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId) {

        if (categoryId == null || categoryId.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderDAO.thongKeDoanhThuTheoDanhMuc(categoryId);
    }
}