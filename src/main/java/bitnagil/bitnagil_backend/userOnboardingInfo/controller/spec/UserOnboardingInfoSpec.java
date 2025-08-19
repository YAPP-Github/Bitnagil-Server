package bitnagil.bitnagil_backend.userOnboardingInfo.controller.spec;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.userOnboardingInfo.response.UserOnboardingInfoSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.ONBOARDING)
public interface UserOnboardingInfoSpec {

    @Operation(summary = "유저의 온보딩 정보를 조회합니다.")
    @ApiErrorCodeExamples({
            ErrorCode.NOT_FOUND_USER_ONBOARDING_INFO
    })
    public CustomResponseDto<UserOnboardingInfoSearchResponse> getUserOnboardingInfo(User user);

}
