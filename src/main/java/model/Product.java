package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description")
    private String  description;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private CartLineItem cartLineItem;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Product product;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private OrderLineItem orderLineItem;

    @ManyToMany(mappedBy = "products")
    private List<Promotion> promotions = new ArrayList<>();
}
