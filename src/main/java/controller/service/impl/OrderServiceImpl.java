package controller.service.impl;

import controller.service.intf.OrderService;
import controller.service.impl.InventoryHistoryServiceImpl;
import controller.service.intf.InventoryHistoryService;
import model.OrderStatus;
import model.PaymentStatus;
import model.dao.impl.OrderDAOImpl;
import model.dao.impl.PaymentDAOImpl;
import model.dao.impl.ProductDAOImpl;
import model.dao.intf.OrderDAO;
import model.dao.intf.PaymentDAO;
import model.dao.intf.ProductDAO;
import model.entity.Order;
import model.entity.InventoryHistory;
import model.entity.OrderHistory;
import model.entity.OrderLineItem;
import model.entity.Payment;
import model.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    private static final List<OrderStatus> FLOW = List.of(
            OrderStatus.PROCESSING, OrderStatus.CONFIRMED,
            OrderStatus.SHIPPING, OrderStatus.COMPLETED);

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final PaymentDAO paymentDAO;
    private final InventoryHistoryService inventoryHistoryService = new InventoryHistoryServiceImpl();

    public OrderServiceImpl() {
        this.orderDAO = new OrderDAOImpl();
        this.productDAO = new ProductDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    @Override
    public void taoDonHang(Order order) {
        if (order == null || order.getOrderLineItems() == null || order.getOrderLineItems().isEmpty())
            throw new IllegalArgumentException("Đơn hàng không có sản phẩm");

        BigDecimal subtotal = BigDecimal.ZERO;
        Map<String, Integer> requestedStock = new LinkedHashMap<>();
        Map<String, Product> products = new LinkedHashMap<>();
        for (OrderLineItem item : order.getOrderLineItems()) {
            if (item == null || item.getProduct() == null || item.getQuantity() <= 0) continue;
            item.setOrder(order);
            if (item.getOrderLineItemId() == null) item.setOrderLineItemId(UUID.randomUUID().toString());
            if (item.getUnitPrice() == null) item.setUnitPrice(item.getProduct().getPrice());
            subtotal = subtotal.add(item.calculateSubtotal());
            String productId = item.getProduct().getProductId();
            requestedStock.merge(productId, item.getQuantity(), Integer::sum);
            products.put(productId, item.getProduct());
        }
        for (Map.Entry<String, Integer> entry : requestedStock.entrySet()) {
            Product product = products.get(entry.getKey());
            if (!product.checkStock(entry.getValue()))
                throw new IllegalArgumentException("Không đủ tồn kho cho sản phẩm " + product.getProductName());
        }
        order.setSubtotal(subtotal);

        BigDecimal discount = order.getDiscount();
        if (discount == null || discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        order.setDiscount(discount);

        BigDecimal ship = order.getShippingFee() == null ? BigDecimal.ZERO : order.getShippingFee();
        order.setShippingFee(ship);
        order.setTotalAmount(subtotal.subtract(discount).add(ship).max(BigDecimal.ZERO));

        if (order.getOrderDate() == null) order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.getHistory().add(new OrderHistory(order, OrderStatus.PROCESSING, "Khách hàng"));

        List<Product> changedProducts = new ArrayList<>();
        boolean orderSaved = false;
        try {
            for (Map.Entry<String, Integer> entry : requestedStock.entrySet()) {
                Product product = products.get(entry.getKey());
                product.updateStock(-entry.getValue());
                if (!productDAO.update(product)) throw new IllegalStateException("Không cập nhật được tồn kho: " + product.getProductName());
                changedProducts.add(product);
            }
            if (!orderDAO.save(order)) throw new IllegalStateException("Không lưu được đơn hàng");
            orderSaved = true;
            for (Map.Entry<String, Integer> entry : requestedStock.entrySet()) {
                Product product = products.get(entry.getKey());
                InventoryHistory record = new InventoryHistory(); record.setProduct(product); record.setOperation("ORDER");
                record.setQuantityChange(-entry.getValue()); record.setStockBefore(product.getQuantity() + entry.getValue());
                record.setStockAfter(product.getQuantity()); record.setNote("Order " + order.getOrderId()); record.setChangedBy("Khách hàng");
                inventoryHistoryService.record(record);
            }
        } catch (RuntimeException e) {
            if (!orderSaved) {
                for (Product product : changedProducts) {
                    product.updateStock(requestedStock.get(product.getProductId()));
                    productDAO.update(product);
                }
            }
            throw e;
        }
    }

    @Override
    public void huyDonHang(String orderId) {
        capNhatTrangThai(orderId, OrderStatus.CANCELLED, "Hệ thống");
    }

    @Override
    public void capNhatTrangThai(String orderId, OrderStatus status) {
        capNhatTrangThai(orderId, status, "Hệ thống");
    }

    @Override
    public void capNhatTrangThai(String orderId, OrderStatus status, String nguoiThucHien) {
        if (orderId == null || orderId.trim().isEmpty() || status == null)
            throw new IllegalArgumentException("Thiếu mã đơn hoặc trạng thái");

        Order o = orderDAO.findById(orderId);
        if (o == null) throw new IllegalArgumentException("Không tìm thấy đơn hàng");

        OrderStatus cur = o.getOrderStatus();
        if (cur == OrderStatus.CANCELLED || cur == OrderStatus.COMPLETED)
            throw new IllegalStateException("Đơn đã đóng, không thể đổi trạng thái");

        Payment pay = paymentDAO.findByOrderId(orderId);

        if (status == OrderStatus.CANCELLED) {
            for (OrderLineItem it : o.getOrderLineItems()) {      // hoàn tồn kho
                Product p = it.getProduct();
                if (p != null) {
                    int before = p.getQuantity();
                    p.updateStock(it.getQuantity());
                    productDAO.update(p);
                    InventoryHistory record = new InventoryHistory(); record.setProduct(p); record.setOperation("ORDER_CANCELLED");
                    record.setQuantityChange(it.getQuantity()); record.setStockBefore(before); record.setStockAfter(p.getQuantity());
                    record.setNote("Order " + orderId); record.setChangedBy(nguoiThucHien); inventoryHistoryService.record(record);
                }
            }
            if (pay != null && pay.getPaymentStatus() == PaymentStatus.COMPLETED) {
                pay.setPaymentStatus(PaymentStatus.REFUNDED);
                paymentDAO.update(pay);
            }
        } else {
            if (FLOW.indexOf(status) != FLOW.indexOf(cur) + 1)
                throw new IllegalStateException("Chỉ được chuyển sang bước kế tiếp");
            if (status == OrderStatus.COMPLETED && pay != null
                    && pay.getPaymentMethod() != null
                    && ("COD".equalsIgnoreCase(pay.getPaymentMethod().name()) || "CASH".equalsIgnoreCase(pay.getPaymentMethod().name()))
                    && pay.getPaymentStatus() == PaymentStatus.PENDING) {
                pay.setPaymentStatus(PaymentStatus.COMPLETED);
                pay.setPaymentDate(LocalDateTime.now());
                paymentDAO.update(pay);
            }
        }

        o.setOrderStatus(status);
        o.getHistory().add(new OrderHistory(o, status, nguoiThucHien));
        if (!orderDAO.update(o)) throw new IllegalStateException("Không lưu được trạng thái đơn hàng");
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
    public List<Order> getDanhSachDonHang() {
        return orderDAO.findAll();
    }

    @Override
    public List<Order> getDonHangTheoTrangThai(OrderStatus status) {
        return status == null ? new ArrayList<>() : orderDAO.findByStatus(status);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoSanPham(String productId) {
        if (productId == null || productId.trim().isEmpty()) return BigDecimal.ZERO;
        return orderDAO.thongKeDoanhThuTheoSanPham(productId);
    }

    @Override
    public BigDecimal thongKeDoanhThuTheoDanhMuc(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) return BigDecimal.ZERO;
        return orderDAO.thongKeDoanhThuTheoDanhMuc(categoryId);
    }
}
