package bitnagil.bitnagil_backend.onboarding.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.onboarding.controller.spec.OnboardingSpec;
import bitnagil.bitnagil_domain.onboarding.dto.request.OnboardingRequest;
import bitnagil.bitnagil_domain.onboarding.dto.request.OnboardingRequestV2;
import bitnagil.bitnagil_domain.onboarding.dto.request.RegistrationRoutinesRequest;
import bitnagil.bitnagil_domain.onboarding.dto.response.OnboardingResponse;
import bitnagil.bitnagil_domain.onboarding.service.OnboardingService;
import bitnagil.bitnagil_domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class OnboardingController implements OnboardingSpec {

    private final OnboardingService onboardingService;

    @Deprecated
    @PostMapping("/v1/onboardings")
    public CustomResponseDto<OnboardingResponse> startOnboarding(@RequestBody OnboardingRequest onboardingRequest,
                                                                @CurrentUser User user) {
        return CustomResponseDto.from(onboardingService.startOnboarding(onboardingRequest, user));
    }

    @PostMapping("/v2/onboardings")
    public CustomResponseDto<OnboardingResponse> startOnboardingV2(@RequestBody OnboardingRequestV2 onboardingRequest,
                                                                 @CurrentUser User user) {
        return CustomResponseDto.from(onboardingService.startOnboardingV2(onboardingRequest, user));
    }

    // 온보딩 루틴 등록 API (V2)
    @PostMapping("/v2/onboardings/routines")
    public CustomResponseDto<Object> registrationRoutinesV2(@RequestBody RegistrationRoutinesRequest registrationRoutinesRequest,
                                                            @CurrentUser User user) {
        onboardingService.registrationRoutinesV2(registrationRoutinesRequest, user);
        return CustomResponseDto.from(null);
    }

    // TODO: v2로 전환 시 deprecated 처리
    @Deprecated()
    @PostMapping("/v1/onboardings/routines")
    public CustomResponseDto<Object> registrationRoutines(@RequestBody RegistrationRoutinesRequest registrationRoutinesRequest,
                                                        @CurrentUser User user) {
        onboardingService.registrationRoutines(registrationRoutinesRequest, user);
        return CustomResponseDto.from(null);
    }
}
