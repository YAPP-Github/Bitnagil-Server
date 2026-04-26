package bitnagil.api.userOnboardingInfo.controller;

import bitnagil.api.global.annotation.CurrentUser;
import bitnagil.api.global.response.CustomResponseDto;
import bitnagil.api.userOnboardingInfo.dto.response.UserOnboardingInfoSearchResponse;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.api.userOnboardingInfo.controller.spec.UserOnboardingInfoSpec;
import bitnagil.bitnagil_domain.userOnboardingInfo.service.UserOnboardingInfoService;
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
        return CustomResponseDto.from(
            UserOnboardingInfoSearchResponse.from(userOnboardingInfoService.getUserOnboardingInfo(user)));
    }
}
