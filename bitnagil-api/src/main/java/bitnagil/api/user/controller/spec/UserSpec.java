package bitnagil.api.user.controller.spec;

import bitnagil.api.global.response.CustomResponseDto;
import bitnagil.api.global.swagger.ApiTags;
import bitnagil.api.user.dto.response.UserInfoResponse;
import bitnagil.api.user.dto.response.UserOnboardingResponse;
import bitnagil.bitnagil_domain.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 유저 API 스펙 정의
 */
@Tag(name = ApiTags.USER)
public interface UserSpec {

    @Operation(summary = "유저 정보를 조회합니다.")
    CustomResponseDto<UserInfoResponse> getUserInfo(User user);

    @Operation(summary = "유저의 온보딩 정보를 조회합니다.")
    CustomResponseDto<UserOnboardingResponse> getUserOnboarding(User user);
}
