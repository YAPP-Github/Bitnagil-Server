package bitnagil.bitnagil_backend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.user.controller.spec.UserSpec;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserInfoResponse;
import bitnagil.bitnagil_backend.user.response.UserOnboardingResponse;
import bitnagil.bitnagil_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserController implements UserSpec {

    private final UserService userService;

    @GetMapping("/infos")
    public CustomResponseDto<UserInfoResponse> getUserInfo(@CurrentUser User user) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(user);

        return CustomResponseDto.from(userInfoResponse);
    }

    @GetMapping("/onboarding")
    public CustomResponseDto<UserOnboardingResponse> getUserOnboarding(@CurrentUser User user) {
        return CustomResponseDto.from(userService.getUserOnboarding(user));
    }
}
