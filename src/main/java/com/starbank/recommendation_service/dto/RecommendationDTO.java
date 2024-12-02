package com.starbank.recommendation_service.dto;

import java.util.Objects;

public class RecommendationDTO {
    private String name;
    private String id;
    private String text;

    public RecommendationDTO(String name, String id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "RecommendationDTO{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationDTO that = (RecommendationDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, text);
    }
}
