package model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @Column(name = "brand_id")
    private String brandId;

    @Nationalized
    @Column(name = "brand_name")
    private String brandName;

    @Nationalized
    @Column(name = "origin", length = 100)
    private String origin;

    @Nationalized
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public String getBrandId() { return brandId; }

    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }

    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<Product> getProducts() { return products; }

    public void setProducts(List<Product> products) { this.products = products; }
}