package model;

import jakarta.persistence.*;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @Column(name = "promotion_id")
    private String promotionId;

    @Column(name = "promotion_name")
    private String promotionName;

    @Enumerated
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(name = "discount_vallue")
    private double discountValue;

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
    private List<Product> products = new ArrayList<>();

    @ManyToMany()
    @JoinTable(
            name = "promotion_customerRank",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "rank_id")
    )
    private List<CustomerRank> customerRanks = new ArrayList<>();
}
