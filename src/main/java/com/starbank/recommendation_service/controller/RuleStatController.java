package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.RuleStat;
import com.starbank.recommendation_service.service.RuleStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleStatController {

    @Autowired
    private RuleStatService ruleStatService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, List<RuleStat>>> getStats() {
        List<RuleStat> stats = ruleStatService.getAllStats();
        Map<String, List<RuleStat>> response = new HashMap<>();
        response.put("stats", stats);
        return ResponseEntity.ok(response);
    }
}
