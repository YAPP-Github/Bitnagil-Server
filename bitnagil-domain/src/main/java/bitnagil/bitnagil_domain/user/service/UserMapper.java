package bitnagil.bitnagil_domain.user.service;

import bitnagil.bitnagil_backend.user.response.UserInfoResponse;
import bitnagil.bitnagil_backend.user.response.UserOnboardingResponse;
import bitnagil.bitnagil_domain.onboarding.domain.enums.EmotionType;
import bitnagil.bitnagil_domain.onboarding.domain.enums.TargetOutingFrequency;
import bitnagil.bitnagil_domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/*
 * 유저 관련 DTO로 변환하는 클래스입니다.
 */
@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
            .nickname(user.getNickname())
            .build();
    }

    public UserOnboardingResponse toUserOnboardingResponse(
        LocalTime timeSlot, EmotionType emotionType, TargetOutingFrequency targetOutingFrequency) {

        return UserOnboardingResponse.builder()
            .timeSlot(timeSlot)
            .emotionType(emotionType)
            .targetOutingFrequency(targetOutingFrequency)
            .build();
    }
}
