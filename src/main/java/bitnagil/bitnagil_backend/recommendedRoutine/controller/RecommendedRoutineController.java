package bitnagil.bitnagil_backend.recommendedRoutine.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.recommendedRoutine.controller.spec.RecommendedRoutineSpec;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResponse;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResult;
import bitnagil.bitnagil_backend.recommendedRoutine.service.RecommendedRoutineService;
import bitnagil.bitnagil_domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/recommend-routines")
public class RecommendedRoutineController implements RecommendedRoutineSpec {
    private final RecommendedRoutineService recommendedRoutineService;

    // 추천 루틴 전체 조회
    @GetMapping("")
    public CustomResponseDto<RecommendedRoutineSearchResponse> searchRecommendedRoutines(@CurrentUser User user) {
        return CustomResponseDto.from(recommendedRoutineService.searchRecommendedRoutines(user));
    }

    // 추천 루틴 단건 조회
    @GetMapping("/{recommendedRoutineId}")
    public CustomResponseDto<RecommendedRoutineSearchResult> searchRecommendedRoutine(@PathVariable Long recommendedRoutineId) {
        return CustomResponseDto.from(recommendedRoutineService.searchRecommendedRoutine(recommendedRoutineId));
    }
}
