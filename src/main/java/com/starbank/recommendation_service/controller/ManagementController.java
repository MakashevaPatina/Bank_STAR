package com.starbank.recommendation_service.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagementController {
    private final BuildProperties buildProperties;

    public ManagementController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping("/management/info")
    public Info getServiceInfo() {
        String name = buildProperties.getName();
        String version = buildProperties.getVersion();
        return new Info(name, version);
    }

    record Info(String name, String version) {
    }
}


