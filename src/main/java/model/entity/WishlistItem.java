package model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wishlist_item", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class WishlistItem {
    @Id
    @Column(name = "wishlist_item_id")
    private String wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public String getWishlistItemId(){ return wishlistItemId; }

    public void setWishlistItemId(String wishlistItemId) {
        this.wishlistItemId = wishlistItemId;
    }
        
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}
