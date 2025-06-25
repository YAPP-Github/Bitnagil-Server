package bitnagil.bitnagil_backend.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.user.controller.spec.UserAuthSpec;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.service.UserAuthService;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class UserAuthController implements UserAuthSpec {
    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public CustomResponseDto<TokenResponse> login(
        @RequestParam("socialType") SocialType socialType,
        @RequestParam(value = "nickname", required = false) String nickname, // 애플로그인 시 nickname은 클라이언트에서 보내준다.
        @RequestHeader("Authorization") String socialAccessToken) {

        TokenResponse tokenResponse = userAuthService.socialLogin(socialType, nickname, socialAccessToken);

        return CustomResponseDto.from(tokenResponse);
    }

    @PostMapping("/logout")
    public CustomResponseDto<Object> logout(
        @CurrentUser User user, HttpServletRequest request,
        @RequestHeader("SocialAccessToken") String socialAccessToken) {
        userAuthService.logout(user, request, socialAccessToken);

        return CustomResponseDto.from(null);
    }

    @PostMapping("/token/reissue")
    public CustomResponseDto<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {

        TokenResponse tokenResponse = userAuthService.reissueToken(refreshToken);

        return CustomResponseDto.from(tokenResponse);
    }

    @PostMapping("/withdrawal")
    public CustomResponseDto<Object> withdrawal(
        @CurrentUser User user, HttpServletRequest request,
        @RequestHeader("SocialAccessToken") String socialAccessToken) {
        userAuthService.withdrawal(user, request, socialAccessToken);

        return CustomResponseDto.from(null);
    }
}
