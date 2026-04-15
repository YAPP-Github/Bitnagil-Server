package bitnagil.bitnagil_domain.onboarding.dto.response;

import bitnagil.bitnagil_domain.recommendedRoutine.dto.response.RecommendedRoutineDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


/**
 * 온보딩 응답에 대한 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
public class OnboardingResponse {

    private List<RecommendedRoutineDto> recommendedRoutines;

}
