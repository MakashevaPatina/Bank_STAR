package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.exception.DynamicRuleNotFoundException;
import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicRuleService {
    private final Logger logger = LoggerFactory.getLogger(DynamicRule.class);

    private final DynamicRulesRepository repository;
    private final RuleStatService ruleStatService;

    public DynamicRuleService(DynamicRulesRepository repository, RuleStatService ruleStatService) {
        this.repository = repository;
        this.ruleStatService = ruleStatService;
    }


    public DynamicRule createDynamicRule(DynamicRule dynamicRule) {
        logger.info("Создание правила c id:{} для {}", dynamicRule.getId(), dynamicRule.getProductName());

        return repository.save(dynamicRule);
    }

    public DynamicRule deleteDynamicRule(Long id) {
        try {
            DynamicRule ruleForDelete = repository.findById(id).orElseThrow(() -> new DynamicRuleNotFoundException(id));
            repository.delete(ruleForDelete);
            logger.info("Удаление правила c id:{}", id);

            ruleStatService.deleteRuleStat(id.toString());

            return ruleForDelete;
        } catch (DynamicRuleNotFoundException e) {
            logger.error("Правило с id:{} не найдено", id, e);
            throw e;
        }

    }

    public List<DynamicRule> findAll() {
        return repository.findAll();
    }
}