package bitnagil.bitnagil_backend.user.controller;

import bitnagil.bitnagil_backend.user.request.UserLoginRequest;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody UserLoginRequest userLoginRequest,
            @RequestHeader("SocialAccessToken") String socialAccessToken) {

        TokenResponse tokenResponse = userAuthService.socialLogin(userLoginRequest.getSocialType(), userLoginRequest.getNickname(), socialAccessToken);

        return CustomResponseDto.from(tokenResponse);
    }

    @PostMapping("/logout")
    public CustomResponseDto<Object> logout(
            @CurrentUser User user,
            @RequestHeader(value = "SocialAccessToken", required = false) String socialAccessToken) {
        userAuthService.logout(user, socialAccessToken);

        return CustomResponseDto.from(null);
    }

    @PostMapping("/token/reissue")
    public CustomResponseDto<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse tokenResponse = userAuthService.reissueToken(refreshToken);

        return CustomResponseDto.from(tokenResponse);
    }

    @PostMapping("/withdrawal")
    public CustomResponseDto<Object> withdrawal(@CurrentUser User user) {
        userAuthService.withdrawal(user);

        return CustomResponseDto.from(null);
    }
}
