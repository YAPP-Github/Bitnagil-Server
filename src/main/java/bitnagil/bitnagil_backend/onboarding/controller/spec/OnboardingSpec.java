package bitnagil.bitnagil_backend.onboarding.controller.spec;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.request.RegistrationRoutinesRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;

@Tag(name = ApiTags.ONBOARDING)
public interface OnboardingSpec {

    @Operation(summary = "온보딩을 수행하고, 추천 루틴을 응답받습니다.")
    @ApiErrorCodeExamples({
            ErrorCode.NOT_FOUND_USER, ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE
    })
    public CustomResponseDto<OnboardingResponse> startOnboarding(OnboardingRequest onboardingRequest, User user);

    @Operation(summary = "온보딩 시 추천 루틴을 등록합니다.(복수 선택이 가능합니다.)")
    @ApiErrorCodeExamples({
            ErrorCode.NOT_FOUND_USER, ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE
    })
    public CustomResponseDto<Null> registrationRoutines(RegistrationRoutinesRequest registrationRoutinesRequest,
                                                        User user);
}
