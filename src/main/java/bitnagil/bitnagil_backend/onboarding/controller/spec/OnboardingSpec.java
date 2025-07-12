package bitnagil.bitnagil_backend.onboarding.controller.spec;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.ONBOARDING)
public interface OnboardingSpec {

    @Operation(summary = "온보딩을 수행하고, 추천 루틴을 응답받습니다.")
    @ApiErrorCodeExamples({
            ErrorCode.NOT_FOUND_USER
    })
    public OnboardingResponse startOnboarding(OnboardingRequest onboardingRequest, User user);
}
