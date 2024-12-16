package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.dto.RuleStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleStatRepository extends JpaRepository<RuleStat, String> {
    List<RuleStat> findAll();
}
