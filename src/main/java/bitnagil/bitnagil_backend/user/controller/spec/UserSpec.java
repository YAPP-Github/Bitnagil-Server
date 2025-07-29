package bitnagil.bitnagil_backend.user.controller.spec;

import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 유저 API 스펙 정의
 */
@Tag(name = ApiTags.USER)
public interface UserSpec {

    @Operation(summary = "유저 정보를 조회합니다.")
    CustomResponseDto<UserInfoResponse> getUserInfo(User user);
}
