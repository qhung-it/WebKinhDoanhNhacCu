package controller.service.impl;

import controller.service.intf.OrderService;
import model.dao.impl.OrderDAOImpl;
import model.dao.intf.OrderDAO;
import model.entity.Order;
import model.entity.OrderLineItem;

import java.math.BigDecimal;
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
                    item.getUnitPrice() == null ||
                    item.getQuantity() <= 0) {
                continue;
            }

            item.setOrder(order);

            BigDecimal thanhTien =
                    item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

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

        BigDecimal totalAmount =
                subtotal.subtract(discount);

        order.setTotalAmount(totalAmount);

        orderDAO.save(order);
    }

    @Override
    public void huyDonHang(String orderId) {

        Order order = orderDAO.findById(orderId);

        if (order == null) {
            return;
        }

        /*
         * Tạm thời chưa xóa Order khỏi database.
         * Trạng thái hủy sẽ được xử lý khi hoàn thiện OrderStatus.
         */
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
    public List<Order> getDonHangTheoTrangThai(String statusId) {
        return orderDAO.findByStatusId(statusId);
    }

    @Override
    public void capNhatTrangThai(String orderId, String statusId) {

        if (orderId == null || statusId == null) {
            return;
        }

        orderDAO.updateStatus(orderId, statusId);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoSanPham(String productId) {

        if (productId == null) {
            return BigDecimal.ZERO;
        }

        return orderDAO.thongKeDoanhThuTheoSanPham(productId);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId) {

        if (categoryId == null) {
            return BigDecimal.ZERO;
        }

        return orderDAO.thongKeDoanhThuTheoDanhMuc(categoryId);
    }
}