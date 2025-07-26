package bitnagil.bitnagil_backend.recommendedRoutine.controller.spec;

import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResponse;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.RECOMMENDED_ROUTINE)
public interface RecommendedRoutineSpec {

    @Operation(summary = "추천 루틴을 조회합니다.")
    @ApiErrorCodeExamples({
            ErrorCode.NOT_FOUND_USER
    })
    CustomResponseDto<RecommendedRoutineSearchResponse> searchRecommendedRoutines(User user);

}
