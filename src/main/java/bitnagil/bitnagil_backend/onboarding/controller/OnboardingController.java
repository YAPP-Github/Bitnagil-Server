package bitnagil.bitnagil_backend.onboarding.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.onboarding.controller.spec.OnboardingSpec;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_backend.onboarding.service.OnboardingService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/onboarding")
public class OnboardingController implements OnboardingSpec {

    private final OnboardingService onboardingService;

    @PostMapping()
    public OnboardingResponse startOnboarding(@RequestBody OnboardingRequest onboardingRequest,
                                              @CurrentUser User user) {
        return onboardingService.startOnboarding(onboardingRequest, user);
    }

}
