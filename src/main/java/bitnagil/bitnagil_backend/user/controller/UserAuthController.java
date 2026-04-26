package bitnagil.bitnagil_backend.user.controller;

import bitnagil.user.dto.request.UserAgreementsRequest;
import bitnagil.user.dto.request.UserLoginRequest;
import org.springframework.web.bind.annotation.*;

import bitnagil.user.dto.request.UserWithdrawalRequest;
import bitnagil.user.dto.response.UserTokenResponse;
import bitnagil.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.user.controller.spec.UserAuthSpec;
import bitnagil.user.domain.User;
import bitnagil.bitnagil_backend.user.service.UserAuthService;
import bitnagil.global.response.CustomResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class UserAuthController implements UserAuthSpec {
    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public CustomResponseDto<UserTokenResponse> login(
            @RequestBody UserLoginRequest userLoginRequest,
            @RequestHeader("SocialAccessToken") String socialAccessToken) {

        UserTokenResponse userTokenResponse = userAuthService.socialLogin(
            userLoginRequest.getSocialType(),
            userLoginRequest.getNickname(),
            socialAccessToken);

        return CustomResponseDto.from(userTokenResponse);
    }

    @PostMapping("/logout")
    public CustomResponseDto<Object> logout(@CurrentUser User user) {
        userAuthService.logout(user);

        return CustomResponseDto.from(null);
    }

    @PostMapping("/token/reissue")
    public CustomResponseDto<UserTokenResponse> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        return CustomResponseDto.from(userAuthService.reissueToken(refreshToken));
    }

    @PostMapping("/withdrawal")
    public CustomResponseDto<Object> withdrawal(@RequestBody UserWithdrawalRequest userWithdrawalRequest,
                                                @CurrentUser User user) {
        userAuthService.withdrawal(userWithdrawalRequest, user);

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
