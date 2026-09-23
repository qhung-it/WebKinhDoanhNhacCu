package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_status")
public class OrderStatus {
    @Id
    @Column(name = "status_id")
    private String statusId;

    @Column(name = "status_name")
    private String statusName;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
