package model.entity;

import jakarta.persistence.*;
import model.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineItem> orderLineItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Deposit deposit;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal result = BigDecimal.ZERO;

        for (OrderLineItem item : orderLineItems) {
            if (item != null) {
                result = result.add(item.calculateSubtotal());
            }
        }

        this.subtotal = result;
        return result;
    }

    public BigDecimal calculateDiscount() {
        if (user == null || user.getCustomerRank() == null) {
            this.discount = BigDecimal.ZERO;
            return this.discount;
        }

        CustomerRank customerRank = user.getCustomerRank();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (OrderLineItem item : orderLineItems) {
            Product product = item.getProduct();

            if (product == null) {
                continue;
            }

            BigDecimal itemSubtotal = item.calculateSubtotal();
            BigDecimal itemDiscount = BigDecimal.ZERO;

            for (Promotion promotion : product.getPromotions()) {

                // Kiểm tra thời gian áp dụng
                if (promotion.getStartDateTime() != null
                        && now.isBefore(promotion.getStartDateTime())) {
                    continue;
                }

                if (promotion.getEndDateTime() != null
                        && now.isAfter(promotion.getEndDateTime())) {
                    continue;
                }

                // Kiểm tra CustomerRank
                if (!promotion.getCustomerRanks().contains(customerRank)) {
                    continue;
                }

                BigDecimal value = promotion.getDiscountValue();

                if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal currentDiscount;

                if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                    currentDiscount = itemSubtotal
                            .multiply(value)
                            .divide(BigDecimal.valueOf(100));
                } else {
                    currentDiscount = value;
                }

                // Không giảm quá giá trị sản phẩm
                if (currentDiscount.compareTo(itemSubtotal) > 0) {
                    currentDiscount = itemSubtotal;
                }

                // Chọn promotion giảm nhiều nhất
                if (currentDiscount.compareTo(itemDiscount) > 0) {
                    itemDiscount = currentDiscount;
                }
            }

            totalDiscount = totalDiscount.add(itemDiscount);
        }

        this.discount = totalDiscount;
        return this.discount;
    }

    public BigDecimal calculateTotal() {
        this.totalAmount = calculateSubtotal()
                .subtract(calculateDiscount());

        return this.totalAmount;
    }

    public void addLineItem(OrderLineItem lineItem) {
        if (lineItem == null) {
            return;
        }

        orderLineItems.add(lineItem);
        lineItem.setOrder(this);
    }
}
