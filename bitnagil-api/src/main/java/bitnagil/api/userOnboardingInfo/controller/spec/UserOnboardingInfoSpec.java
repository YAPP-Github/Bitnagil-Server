package bitnagil.api.userOnboardingInfo.controller.spec;

import bitnagil.common.errorcode.ErrorCode;
import bitnagil.api.global.response.CustomResponseDto;
import bitnagil.api.global.swagger.ApiErrorCodeExamples;
import bitnagil.api.global.swagger.ApiTags;
import bitnagil.api.userOnboardingInfo.dto.response.UserOnboardingInfoSearchResponse;
import bitnagil.bitnagil_domain.user.domain.User;
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
