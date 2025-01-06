package com.starbank.recommendation_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rule_stats")
public class RuleStat {
    @Id
    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "count")
    private Integer count;

    public RuleStat() {
    }

    public RuleStat(String ruleId, int count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
