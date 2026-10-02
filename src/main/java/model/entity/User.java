package model.entity;

import jakarta.persistence.*;
import model.RankName;
import model.Role;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id")
    private CustomerRank customerRank;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public CustomerRank getCustomerRank() {
        return customerRank;
    }

    public void setCustomerRank(CustomerRank customerRank) {
        this.customerRank = customerRank;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void updateProfile(String fullName, String email, String phone, String address) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || !this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }

        if (newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu cũ");
        }

        this.password = newPassword;
    }

    public RankName calculateRank(BigDecimal totalSpending) {
        if (totalSpending == null) {
            return null;
        }

        BigDecimal bronze = new BigDecimal("100000000");
        BigDecimal silver = new BigDecimal("150000000");
        BigDecimal gold = new BigDecimal("250000000");
        BigDecimal vip = new BigDecimal("500000000");

        if (totalSpending.compareTo(vip) >= 0) {
            return RankName.VIP;
        }

        if (totalSpending.compareTo(gold) >= 0) {
            return RankName.GOLD;
        }

        if (totalSpending.compareTo(silver) >= 0) {
            return RankName.SILVER;
        }

        if (totalSpending.compareTo(bronze) >= 0) {
            return RankName.BRONZE;
        }

        return null;
    }
}
