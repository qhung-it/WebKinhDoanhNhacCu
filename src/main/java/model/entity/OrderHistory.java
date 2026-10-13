package model.entity;

import jakarta.persistence.*;
import model.OrderStatus;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_history")
public class OrderHistory {
    @Id
    @Column(name = "history_id")
    private String historyId = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private OrderStatus status;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Nationalized
    @Column(name = "changed_by", length = 100)
    private String changedBy;

    public OrderHistory() {}

    public OrderHistory(Order order, OrderStatus status, String changedBy) {
        this.order = order;
        this.status = status;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}