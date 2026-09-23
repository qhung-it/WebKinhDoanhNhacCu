package model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_rank")
public class CustomerRank {
    @Id
    @Column(name = "rank_id")
    private String rankId;

    @Column(name = "rank_name")
    private String rankName;

    @Column(name = "min_spending")
    private BigDecimal minSpending;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "customerRank")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "customerRanks")
    private List<Promotion> promotions = new ArrayList<>();

    public String getRankId() {
        return rankId;
    }

    public void setRankId(String rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public BigDecimal getMinSpending() {
        return minSpending;
    }

    public void setMinSpending(BigDecimal minSpending) {
        this.minSpending = minSpending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }
}
