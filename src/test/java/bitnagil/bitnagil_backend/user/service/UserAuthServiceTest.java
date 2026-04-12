package bitnagil.bitnagil_backend.user.service;

import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.jwt.AuthRedisService;
import bitnagil.bitnagil_backend.auth.jwt.JwtUtil;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoService;
import bitnagil.bitnagil_domain.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("회원 인증 테스트")
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @InjectMocks
    UserAuthService userAuthService;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthRedisService authRedisService;
    @Mock
    AppleUserInfoService appleUserInfoService;
    @Mock
    KakaoUserInfoService kakaoUserInfoService;
    @Mock
    UserManager userManager;

    @Test
    @DisplayName("약관 동의 테스트 - 약관 동의를 수행하면 USER의 ROLE이 USER로 변경된다.")
    void whenAgreeToTerms_thenRoleChangesFromGuestToUser(){
        //TODO 리팩터링 예정

        // given
        // UUID uuid = UUID.randomUUID();
        //
        // User user = User.builder()
        //     .userPk(new HistoryPk(uuid, 1L))
        //     .socialType(SocialType.APPLE)
        //     .role(Role.GUEST) // 초기 ROLE은 GUEST
        //     .email("test@naver.com")
        //     .nickname("테스트유저")
        //     .refreshToken("refreshToken")
        //     .build();
        //
        // UserAgreementsRequest reqeust = new UserAgreementsRequest(true, true, true);
        // when(userRepository.findByUserPk(any(HistoryPk.class))).thenReturn(Optional.of(user)); // mocking
        //
        // // when
        // userAuthService.agreements(reqeust, user);
        //
        // // then
        // assertEquals(Role.USER, user.getRole(), "약관 동의 후 ROLE이 USER로 변경되어야 합니다.");
        // assertTrue(user.getAgreedToTermsOfService(), "서비스 이용약관 동의가 true여야 합니다.");
        // assertTrue(user.getAgreedToPrivacyPolicy(), "개인정보 수집 동의가 true여야 합니다.");
        // assertTrue(user.getIsOverFourteen(), "14세 이상 여부가 true여야 합니다.");
    }
}