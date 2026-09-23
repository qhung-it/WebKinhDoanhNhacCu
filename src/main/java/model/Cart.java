package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @Column(name = "cart_id")
    private String cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartLineItem> cartLineItems = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartLineItem> getCartLineItems() {
        return cartLineItems;
    }

    public void setCartLineItems(List<CartLineItem> cartLineItems) {
        this.cartLineItems = cartLineItems;
    }
}
