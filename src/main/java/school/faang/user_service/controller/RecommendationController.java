package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public void giveRecommendation(@RequestBody RecommendationDto recommendation){
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }

    private void validateRecommendation(RecommendationDto recommendationDto){
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }
}