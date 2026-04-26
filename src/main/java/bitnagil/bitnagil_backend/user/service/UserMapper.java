package bitnagil.bitnagil_backend.user.service;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import bitnagil.onboarding.domain.enums.EmotionType;
import bitnagil.onboarding.domain.enums.TargetOutingFrequency;
import bitnagil.user.domain.User;
import bitnagil.user.dto.response.UserInfoResponse;
import bitnagil.user.dto.response.UserOnboardingResponse;

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
