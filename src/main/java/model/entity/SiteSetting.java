package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_setting")
public class SiteSetting {
    @Id
    @Column(name = "setting_id", length = 40)
    private String settingId = "default";

    @Column(name = "free_shipping_from", precision = 18, scale = 2)
    private BigDecimal freeShippingFrom = new BigDecimal("500000");

    @Column(name = "shipping_fee", precision = 18, scale = 2)
    private BigDecimal shippingFee = new BigDecimal("30000");

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public String getSettingId() { return settingId; }
    public void setSettingId(String settingId) { this.settingId = settingId; }

    public BigDecimal getFreeShippingFrom() { return freeShippingFrom; }
    public void setFreeShippingFrom(BigDecimal freeShippingFrom) { this.freeShippingFrom = freeShippingFrom; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
