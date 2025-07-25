package bitnagil.bitnagil_backend.recommendedRoutine.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResponse;
import bitnagil.bitnagil_backend.recommendedRoutine.service.RecommendedRoutineService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/recommend-routines")
public class RecommendedRoutineController {
    private final RecommendedRoutineService recommendedRoutineService;

    @GetMapping("")
    public CustomResponseDto<RecommendedRoutineSearchResponse> searchRecommendedRoutines(@CurrentUser User user) {
        return CustomResponseDto.from(recommendedRoutineService.searchRecommendedRoutines(user));
    }
}
