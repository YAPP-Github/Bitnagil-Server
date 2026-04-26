package bitnagil.api.user.dto.response;

import bitnagil.bitnagil_domain.onboarding.domain.enums.EmotionType;
import bitnagil.bitnagil_domain.onboarding.domain.enums.TargetOutingFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class UserOnboardingResponse {
    private LocalTime timeSlot;
    private EmotionType emotionType;
    private TargetOutingFrequency targetOutingFrequency;

    public static UserOnboardingResponse from(
        bitnagil.bitnagil_domain.user.dto.response.UserOnboardingResponse domainResponse) {
        return UserOnboardingResponse.builder()
            .timeSlot(domainResponse.getTimeSlot())
            .emotionType(domainResponse.getEmotionType())
            .targetOutingFrequency(domainResponse.getTargetOutingFrequency())
            .build();
    }
}
