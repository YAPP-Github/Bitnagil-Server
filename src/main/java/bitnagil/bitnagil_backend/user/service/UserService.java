package bitnagil.bitnagil_backend.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_domain.onboarding.domain.Onboarding;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserInfoResponse;
import bitnagil.bitnagil_backend.user.response.UserOnboardingResponse;
import lombok.RequiredArgsConstructor;

/**
 * 유저 정보를 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public UserInfoResponse getUserInfo(User user) {
        return userMapper.toUserInfoResponse(user);
    }

    @Transactional(readOnly = true)
    public UserOnboardingResponse getUserOnboarding(User user) {
        Onboarding onboarding = user.getOnboarding();

        return userMapper.toUserOnboardingResponse(
            onboarding.getTimeSlot(),
            onboarding.getEmotionType(),
            onboarding.getTargetOutingFrequency());
    }
}
