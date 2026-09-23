package model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_line_item")
public class CartLineItem {
    @Id
    @Column(name = "cart_line_item_id")
    private String cartLineItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public String getCartLineItemId() {
        return cartLineItemId;
    }

    public void setCartLineItemId(String cartLineItemId) {
        this.cartLineItemId = cartLineItemId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
