package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.repository.DynamicRulesRepository;
import com.starbank.recommendation_service.rules.dynamic.Condition;
import com.starbank.recommendation_service.rules.dynamic.DynamicRule;
import com.starbank.recommendation_service.service.DynamicRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rule")
@Tag(name = "Контроллер правил")
public class DynamicRuleController {

    private final DynamicRuleService service;

    public DynamicRuleController(DynamicRuleService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Создание правила",
            description = "Id проставляется из репозитория")
    public DynamicRule createDynamicRule(@RequestBody DynamicRule dynamicRule) {
        return service.createDynamicRule(dynamicRule);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление правила")
    public DynamicRule deleteDynamicRule(@RequestParam("id") long id) {
        return service.deleteDynamicRule(id);
    }

    @GetMapping("/find_all")
    @Operation(summary = "Получение списка всех правил")
    public List<DynamicRule> findAll() {
        return service.findAll();
    }
}
