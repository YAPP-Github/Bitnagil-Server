package bitnagil.bitnagil_backend.userOnboardingInfo.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_backend.userOnboardingInfo.controller.spec.UserOnboardingInfoSpec;
import bitnagil.bitnagil_backend.userOnboardingInfo.response.UserOnboardingInfoSearchResponse;
import bitnagil.bitnagil_backend.userOnboardingInfo.service.UserOnboardingInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserOnboardingInfoController implements UserOnboardingInfoSpec {

    private final UserOnboardingInfoService userOnboardingInfoService;

    @GetMapping("/v2/onboardings")
    public CustomResponseDto<UserOnboardingInfoSearchResponse> getUserOnboardingInfo(@CurrentUser User user) {
        return CustomResponseDto.from(userOnboardingInfoService.getUserOnboardingInfo(user));
    }
}
