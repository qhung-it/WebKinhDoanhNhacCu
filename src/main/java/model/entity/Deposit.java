package model.entity;

import jakarta.persistence.*;
import model.DepositStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit")
public class Deposit {
    @Id
    @Column(name = "deposit_id")
    private String depositId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "deposit_date")
    private LocalDateTime depositDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_status", nullable = false)
    private DepositStatus depositStatus = DepositStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDateTime depositDate) {
        this.depositDate = depositDate;
    }

    public DepositStatus getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(DepositStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal calculateDeposit() {
        if (order == null || order.getTotalAmount() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAmount = order.getTotalAmount();
        BigDecimal minimumAmount = new BigDecimal("100000000");

        if (totalAmount.compareTo(minimumAmount) < 0) {
            return BigDecimal.ZERO;
        }

        return totalAmount.multiply(new BigDecimal("0.30"));
    }

    public void processDeposit() {
        this.amount = calculateDeposit();
        this.depositStatus = DepositStatus.COMPLETED;
        this.depositDate = LocalDateTime.now();
    }

    public boolean checkDepositStatus() {
        return this.depositStatus == DepositStatus.COMPLETED;
    }
}
