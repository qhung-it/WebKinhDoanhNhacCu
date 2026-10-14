package model.entity;

import jakarta.persistence.*;
import model.DiscountType;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @Column(name = "promotion_id")
    private String promotionId;

    @Nationalized
    @Column(name = "promotion_name")
    private String promotionName;

    @Column(name = "coupon_code", length = 20)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(name = "discount_value", precision = 19, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "minimum_order_amount", precision = 19, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(name = "max_discount", precision = 19, scale = 2)
    private BigDecimal maxDiscount;

    @Nationalized
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "free_shipping")
    // Existing databases may contain NULL despite this flag defaulting to false.
    // Use the wrapper so Hibernate can read those legacy rows without failing.
    private Boolean freeShipping = false;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ManyToMany()
    @JoinTable(
            name = "promotion_product",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new LinkedHashSet<>();

    @ManyToMany()
    @JoinTable(
            name = "promotion_customer_rank",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "rank_id")
    )
    private Set<CustomerRank> customerRanks = new LinkedHashSet<>();

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
    
    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }
    
    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isFreeShipping() {
        return Boolean.TRUE.equals(freeShipping);
    }
    
    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }
    
    public boolean getActive() {
        return active == null || active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products == null ? new LinkedHashSet<>() : new LinkedHashSet<>(products);
    }

    public Set<CustomerRank> getCustomerRanks() {
        return customerRanks;
    }

    public void setCustomerRanks(Collection<CustomerRank> customerRanks) {
        this.customerRanks = customerRanks == null ? new LinkedHashSet<>() : new LinkedHashSet<>(customerRanks);
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();

        if (Boolean.FALSE.equals(active)) return false;

        if (startDateTime != null && now.isBefore(startDateTime)) {
            return false;
        }

        if (endDateTime != null && now.isAfter(endDateTime)) {
            return false;
        }

        return true;
    }

    public boolean isApplicable(Product product, CustomerRank customerRank) {
        if (!isActive()) {
            return false;
        }

        if (product == null || customerRank == null) {
            return false;
        }

        boolean productApplicable = products.contains(product);
        boolean rankApplicable = customerRanks.contains(customerRank);

        return productApplicable && rankApplicable;
    }

    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;

        if (discountType == DiscountType.PERCENTAGE) {
            discount = amount
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100));
        } else {
            discount = discountValue;
        }

        if (maxDiscount != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0
                && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }

        // Không cho tiền giảm lớn hơn giá trị được áp dụng promotion
        if (discount.compareTo(amount) > 0) {
            discount = amount;
        }

        return discount;
    }
}
