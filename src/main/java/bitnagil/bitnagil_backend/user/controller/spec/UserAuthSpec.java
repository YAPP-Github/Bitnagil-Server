package bitnagil.bitnagil_backend.user.controller.spec;

import bitnagil.bitnagil_backend.user.request.UserLoginRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExample;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 유저 인증 API 스펙 정의
 */
@Tag(name = ApiTags.USER_AUTH)
public interface UserAuthSpec {

    @Operation(summary = "소셜회원가입 및 로그인을 수행하고 토큰을 발행합니다.")
    @ApiErrorCodeExamples({
            ErrorCode.KAKAO_FEIGN_CALL_FAILED, ErrorCode.KAKAO_USER_INFO_FAILED, ErrorCode.TOKEN_DECODE_ERROR,
            ErrorCode.APPLE_FEIGN_CALL_FAILED, ErrorCode.INTERNAL_SERVER_ERROR
    })
    @Parameters({
            @Parameter(name = "SocialAccessToken", description = "소셜로그인 플랫폼에서 발급해준 access token 입니다.(Bearer를 붙히지 않습니다.)", required = true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", in = ParameterIn.HEADER),
    })
    CustomResponseDto<TokenResponse> login(UserLoginRequest userLoginRequest,
                                           String socialAccessToken);


    @Operation(summary = "유저가 로그아웃합니다. 반환 정보는 없습니다.")
    @ApiErrorCodeExamples({
            ErrorCode.KAKAO_FEIGN_CALL_FAILED, ErrorCode.KAKAO_LOGOUT_FAILED
    })
    CustomResponseDto<Object> logout(User user);


    @Operation(summary = "토큰 재발급 요청으로 토큰 관련 정보를 반환합니다.")
    @ApiErrorCodeExamples({
            ErrorCode.INVALID_JWT_TOKEN, ErrorCode.NOT_FOUND_USER
    })
    @Parameters({
            @Parameter(name = "Refresh-Token", description = "서버에서 발급해준 refresh token 입니다.(Bearer를 붙히지 않습니다.)", required = true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", in = ParameterIn.HEADER)
    })
    CustomResponseDto<TokenResponse> refreshToken(String refreshToken);


    @Operation(summary = "소셜로그인으로 연결된 유저가 회원탈퇴합니다. 반환 정보는 없습니다.")
    @ApiErrorCodeExamples({
            ErrorCode.KAKAO_FEIGN_CALL_FAILED, ErrorCode.KAKAO_UNLINK_FAILED, ErrorCode.APPLE_FEIGN_CALL_FAILED
    })
    CustomResponseDto<Object> withdrawal(User user);
}