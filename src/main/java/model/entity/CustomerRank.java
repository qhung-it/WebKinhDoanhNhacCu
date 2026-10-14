package model.entity;

import jakarta.persistence.*;
import model.RankName;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customer_rank")
public class CustomerRank {
    @Id
    @Column(name = "rank_id")
    private String rankId;

    @Nationalized
    @Enumerated(EnumType.STRING)
    @Column(name = "rank_name")
    private RankName rankName;

    @Column(name = "min_spending", precision = 19, scale = 2)
    private BigDecimal minSpending;

    @OneToMany(mappedBy = "customerRank")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "customerRanks")
    private Set<Promotion> promotions = new LinkedHashSet<>();

    public String getRankId() {
        return rankId;
    }

    public void setRankId(String rankId) {
        this.rankId = rankId;
    }

    public RankName getRankName() {
        return rankName;
    }

    public void setRankName(RankName rankName) {
        this.rankName = rankName;
    }

    public BigDecimal getMinSpending() {
        return minSpending;
    }

    public void setMinSpending(BigDecimal minSpending) {
        this.minSpending = minSpending;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Set<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(Collection<Promotion> promotions) {
        this.promotions = promotions == null ? new LinkedHashSet<>() : new LinkedHashSet<>(promotions);
    }

    public boolean isEligible(BigDecimal totalSpending) {
        if (totalSpending == null || minSpending == null) {
            return false;
        }

        return totalSpending.compareTo(minSpending) >= 0;
    }
}
