package bitnagil.api.userOnboardingInfo.dto.response;

import bitnagil.bitnagil_domain.onboarding.domain.enums.TargetOutingFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserOnboardingInfoSearchResponse {
    private LocalTime timeSlot;
    private List<String> emotionTypes;
    private TargetOutingFrequency targetOutingFrequency;

    public static UserOnboardingInfoSearchResponse from(
        bitnagil.bitnagil_domain.userOnboardingInfo.response.UserOnboardingInfoSearchResponse domainResponse) {
        return UserOnboardingInfoSearchResponse.builder()
            .timeSlot(domainResponse.getTimeSlot())
            .emotionTypes(domainResponse.getEmotionTypes())
            .targetOutingFrequency(domainResponse.getTargetOutingFrequency())
            .build();
    }
}
