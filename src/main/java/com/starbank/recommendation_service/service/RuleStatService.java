package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RuleStat;
import com.starbank.recommendation_service.repository.RuleStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleStatService {

    @Autowired
    private RuleStatRepository ruleStatRepository;

    public void incrementRuleStat(String ruleId) {
        RuleStat ruleStat = ruleStatRepository.findById(ruleId)
                .orElse(new RuleStat(ruleId, 0));
        ruleStat.setCount(ruleStat.getCount() + 1);
        ruleStatRepository.save(ruleStat);
    }

    public void deleteRuleStat(String ruleId) {
        ruleStatRepository.deleteById(ruleId);
    }

    public List<RuleStat> getAllStats() {
        List<RuleStat> ruleStats = ruleStatRepository.findAll();

        for (RuleStat stat : ruleStats) {
            if (stat.getCount() == null) {
                stat.setCount(0);
            }
        }

        return ruleStats;
    }
}
