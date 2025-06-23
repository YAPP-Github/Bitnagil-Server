package bitnagil.bitnagil_backend.user.controller.spec;

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
    @Operation(summary = "소셜로그인 요청으로 토큰 관련 정보를 반환합니다.")
    @ApiErrorCodeExamples({
        ErrorCode.KAKAO_UNAUTHORIZED, ErrorCode.KAKAO_UNKNOWN_ERROR, ErrorCode.KAKAO_RETRY_INTERRUPTED,
        ErrorCode.TOKEN_DECODE_ERROR, ErrorCode.INTERNAL_SERVER_ERROR
    })
    @Parameters({
        @Parameter(name = "socialType", description = "social login type", required = true, example = "KAKAO"),
        @Parameter(name = "nickname", description = "user's social nickname", required = false, example = "yuseok"),
        @Parameter(name = "Authorization", description = "access tokens issued by social platforms", required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER)
    })
    CustomResponseDto<TokenResponse> login(
        @RequestParam("socialType") SocialType socialType,
        @RequestParam(value = "nickname", required = false) String nickname,
        @RequestHeader("Authorization") String socialAccessToken);

    @Operation(summary = "유저가 로그아웃합니다. 반환 정보는 없습니다.")
    @ApiErrorCodeExamples({
        ErrorCode.KAKAO_UNAUTHORIZED, ErrorCode.KAKAO_UNKNOWN_ERROR, ErrorCode.KAKAO_RETRY_INTERRUPTED
    })
    @Parameters({
        @Parameter(name = "Authorization", description = "JWT access token (Bearer {token})", required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER),
        @Parameter(name = "SocialAccessToken", description = "social access token", required = true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER)
    })
    CustomResponseDto<Object> logout(@CurrentUser User user, HttpServletRequest request,
        @RequestHeader("SocialAccessToken") String socialAccessToken);

    @Operation(summary = "토큰 재발급 요청으로 토큰 관련 정보를 반환합니다.")
    @ApiErrorCodeExamples({
        ErrorCode.INVALID_JWT_TOKEN, ErrorCode.NOT_FOUND_USER
    })
    @Parameters({
        @Parameter(name = "Refresh-Token", description = "리프레시 토큰", required = true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER)
    })
    CustomResponseDto<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken);

    @Operation(summary = "소셜로그인으로 연결된 유저가 회원탈퇴합니다. 반환 정보는 없습니다.")
    @ApiErrorCodeExamples({
        ErrorCode.KAKAO_UNAUTHORIZED, ErrorCode.KAKAO_UNKNOWN_ERROR, ErrorCode.KAKAO_RETRY_INTERRUPTED
    })
    @Parameters({
        @Parameter(name = "Authorization", description = "JWT access token (Bearer {token})", required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER),
        @Parameter(name = "SocialAccessToken", description = "social access token", required = true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", in = ParameterIn.HEADER)
    })
    CustomResponseDto<Object> withdrawal(@CurrentUser User user, HttpServletRequest request,
        @RequestHeader("SocialAccessToken") String socialAccessToken);
}