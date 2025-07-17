package bitnagil.bitnagil_backend.onboarding.repository;

import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.onboarding.domain.enums.EmotionType;
import bitnagil.bitnagil_backend.onboarding.domain.enums.RealOutingFrequency;
import bitnagil.bitnagil_backend.onboarding.domain.enums.TargetOutingFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {
    Onboarding findByTimeSlotAndEmotionTypeAndRealOutingFrequencyAndTargetOutingFrequency(
            LocalTime timeSlot,
            EmotionType emotionType,
            RealOutingFrequency realOutingFrequency,
            TargetOutingFrequency targetOutingFrequency
    );
}
