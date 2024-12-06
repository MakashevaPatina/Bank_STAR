package com.starbank.recommendation_service.rules.dynamic;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class DynamicRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String productText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dynamic_rule_id")
    private List<Condition> conditions;

    public DynamicRule(String productName, String productText, List<Condition> conditions) {
        this.productName = productName;
        this.productText = productText;
        this.conditions = conditions;
    }

    public DynamicRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "DynamicRule{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productText='" + productText + '\'' +
                ", conditions=" + conditions +
                '}';
    }
}