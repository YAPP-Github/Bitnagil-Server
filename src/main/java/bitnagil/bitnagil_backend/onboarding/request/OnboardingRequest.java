package bitnagil.bitnagil_backend.onboarding.request;


import bitnagil.bitnagil_backend.onboarding.domain.enums.EmotionType;
import bitnagil.bitnagil_backend.onboarding.domain.enums.RealOutingFrequency;
import bitnagil.bitnagil_backend.onboarding.domain.enums.TargetOutingFrequency;
import bitnagil.bitnagil_backend.onboarding.domain.enums.TimeSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "온보딩 요청 DTO")
@AllArgsConstructor
public class OnboardingRequest {

    @Schema(description = "시간대", required = true)
    private TimeSlot timeSlot;
    @Schema(description = "서비스 이용약관 동의", required = true)
    private EmotionType emotionType;
    @Schema(description = "서비스 이용약관 동의", required = true)
    private RealOutingFrequency realOutingFrequency;
    @Schema(description = "서비스 이용약관 동의",  required = true)
    private TargetOutingFrequency targetOutingFrequency;

}
