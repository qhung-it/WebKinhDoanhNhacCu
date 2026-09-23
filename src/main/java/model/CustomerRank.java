package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "customer_rank")
public class CustomerRank {
    @Id
    @Column(name = "rank_id")
    private String rankId;

    @Column(name = "rank_name")
    private String rankName;

    @Column(name = "min_spending")
    private double minSpending;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "customerRank")
    private List<User> user = new ArrayList<>();

    @ManyToMany(mappedBy = "customer_rank")
    private List<Promotion> promotions = new ArrayList<>();
}
