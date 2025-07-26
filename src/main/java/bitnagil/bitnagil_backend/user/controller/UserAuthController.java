package bitnagil.bitnagil_backend.user.controller;

import bitnagil.bitnagil_backend.user.request.UserAgreementsRequest;
import bitnagil.bitnagil_backend.user.request.UserLoginRequest;
import org.springframework.web.bind.annotation.*;

import bitnagil.bitnagil_backend.user.response.UserLoginResponse;
import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.user.controller.spec.UserAuthSpec;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserReissueResponse;
import bitnagil.bitnagil_backend.user.service.UserAuthService;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class UserAuthController implements UserAuthSpec {
    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public CustomResponseDto<UserLoginResponse> login(
            @RequestBody UserLoginRequest userLoginRequest,
            @RequestHeader("SocialAccessToken") String socialAccessToken) {

        UserLoginResponse userLoginResponse = userAuthService.socialLogin(
            userLoginRequest.getSocialType(),
            userLoginRequest.getNickname(),
            socialAccessToken);

        return CustomResponseDto.from(userLoginResponse);
    }

    @PostMapping("/logout")
    public CustomResponseDto<Object> logout(@CurrentUser User user) {
        userAuthService.logout(user);

        return CustomResponseDto.from(null);
    }

    @PostMapping("/token/reissue")
    public CustomResponseDto<UserReissueResponse> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        UserReissueResponse userReissueResponse = userAuthService.reissueToken(refreshToken);

        return CustomResponseDto.from(userReissueResponse);
    }

    @PostMapping("/withdrawal")
    public CustomResponseDto<Object> withdrawal(@CurrentUser User user) {
        userAuthService.withdrawal(user);

        return CustomResponseDto.from(null);
    }

    // 약관 동의 api
    @PostMapping("/agreements")
    public CustomResponseDto<Void> agreements(@RequestBody UserAgreementsRequest userAgreementsRequest,
                                              @CurrentUser User user) {
        userAuthService.agreements(userAgreementsRequest, user);
        return CustomResponseDto.from(null);
    }

}
