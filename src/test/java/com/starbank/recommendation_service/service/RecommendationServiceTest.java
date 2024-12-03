package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import com.starbank.recommendation_service.rules.RuleInvest500;
import com.starbank.recommendation_service.rules.RuleSimpleCredit;
import com.starbank.recommendation_service.rules.RuleTopSaving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService service;

    @Mock
    private List<RecommendationRuleSet> ruleSets;

    @Mock
    private RuleInvest500 invest500;

    @Mock
    private RuleSimpleCredit simpleCredit;

    @Mock
    private RuleTopSaving topSaving;

    @BeforeEach
    void setUp() {
        ruleSets = List.of(invest500, simpleCredit, topSaving);
        service = new RecommendationService(ruleSets);
    }

    @Test
    void getRecommendations() {
        String userId = "1";

        String recommendationName = "SimpleCredit";
        String recommendationId = "1";
        String recommendationText = "try new credit";

        String recommendationName1 = "Invest500";
        String recommendationId1 = "1";
        String recommendationText1 = "try new invest strategy";

        String recommendationName2 = "TopSaving";
        String recommendationId2 = "1";
        String recommendationText2 = "try new saving";

        RecommendationDTO credit = new RecommendationDTO(recommendationName, recommendationId, recommendationText);
        RecommendationDTO invest = new RecommendationDTO(recommendationName1, recommendationId1, recommendationText1);
        RecommendationDTO saving = new RecommendationDTO(recommendationName2, recommendationId2, recommendationText2);
        List<RecommendationDTO> recommendationDTOList = List.of(invest, credit, saving);
        RecommendationResponse expected = new RecommendationResponse(recommendationDTOList, userId);

        when(simpleCredit.getRecommendation("1")).thenReturn(Optional.of(credit));
        when(topSaving.getRecommendation("1")).thenReturn(Optional.of(saving));
        when(invest500.getRecommendation("1")).thenReturn(Optional.of(invest));

        RecommendationResponse actual = service.getRecommendations(userId);

        assertThat(expected).isEqualTo(actual);
    }
}