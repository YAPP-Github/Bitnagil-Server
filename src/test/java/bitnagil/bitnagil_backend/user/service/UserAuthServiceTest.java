package bitnagil.bitnagil_backend.user.service;

import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.jwt.AuthRedisService;
import bitnagil.bitnagil_backend.auth.jwt.JwtProvider;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoService;
import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import bitnagil.bitnagil_backend.user.request.UserAgreementsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("회원 인증 테스트")
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @InjectMocks
    UserAuthService userAuthService;
    @Mock
    JwtProvider jwtProvider;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthRedisService authRedisService;
    @Mock
    AppleUserInfoService appleUserInfoService;
    @Mock
    KakaoUserInfoService kakaoUserInfoService;

    @Test
    @DisplayName("약관 동의 테스트 - 약관 동의를 수행하면 USER의 ROLE이 USER로 변경된다.")
    void whenAgreeToTerms_thenRoleChangesFromGuestToUser(){
        // given
        User user = User.builder()
                .socialType(SocialType.APPLE)
                .role(Role.GUEST) // 초기 ROLE은 GUEST
                .email("test@naver.com")
                .nickname("테스트유저")
                .refreshToken("refreshToken")
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);

        UserAgreementsRequest reqeust = new UserAgreementsRequest(true, true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // mocking

        // when
        userAuthService.agreements(reqeust, user);

        // then
        assertEquals(Role.USER, user.getRole(), "약관 동의 후 ROLE이 USER로 변경되어야 합니다.");
        assertTrue(user.getAgreedToTermsOfService(), "서비스 이용약관 동의가 true여야 합니다.");
        assertTrue(user.getAgreedToPrivacyPolicy(), "개인정보 수집 동의가 true여야 합니다.");
        assertTrue(user.getIsOverFourteen(), "14세 이상 여부가 true여야 합니다.");
    }
}