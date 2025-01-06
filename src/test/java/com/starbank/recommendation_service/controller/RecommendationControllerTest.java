/*
package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.service.RecommendationService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RecommendationService service;

    @MockitoBean
    RecommendationsRepository repository;


    @Test
    void getRecommendations() throws Exception {
        String userId = "cd515076-5d8a-44be-930e-8d4fcb79f42d";

        String recommendationName = "SimpleCredit";
        String recommendationId = "SimpleCredit";
        String recommendationText = "SimpleCredit";

        RecommendationDTO recommendationDTO = new RecommendationDTO(recommendationName, recommendationId, recommendationText);
        List<RecommendationDTO> recommendationDTOList = List.of(recommendationDTO);
        RecommendationResponse response = new RecommendationResponse(recommendationDTOList, userId);


        JSONObject responseObject = new JSONObject();
        responseObject.put("name", recommendationDTO.getName());
        responseObject.put("id", recommendationDTO.getId());
        responseObject.put("text", recommendationDTO.getText());

        when(service.getRecommendations(userId)).thenReturn(response);
        when(repository.checkRecommendationInvest500(userId)).thenReturn(true);
        when(repository.checkRecommendationSimpleCredit(userId)).thenReturn(true);
        when(repository.checkRecommendationTopSaving(userId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.recommendations[0].name").value(recommendationName))
                .andExpect(jsonPath("$.recommendations[0].id").value(recommendationId))
                .andExpect(jsonPath("$.recommendations[0].text").value(recommendationText));
    }
}
*/
