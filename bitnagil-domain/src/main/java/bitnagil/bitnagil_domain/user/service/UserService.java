package bitnagil.bitnagil_domain.user.service;

import bitnagil.bitnagil_backend.user.response.UserOnboardingResponse;
import bitnagil.bitnagil_domain.onboarding.domain.Onboarding;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_domain.user.domain.enums.Role;
import bitnagil.bitnagil_domain.user.domain.enums.SocialType;
import bitnagil.bitnagil_domain.user.repository.UserRepository;
import bitnagil.common.errorcode.ErrorCode;
import bitnagil.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 유저 정보를 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserManager userManager;
    private final UserRepository userRepository;

    // 소셜 로그인을 통해 로그인 혹은 회원가입을 진행
    // response DTO는 user 정보만 반환되도록 수정한다.
    @Transactional
    public UserTokenResponse socialLogin(SocialType socialType, String nickname, String socialAccessToken) {
        User user = signUpOrLogin(socialType, nickname, userAuthInfo);
        return bitnagil.bitnagil_backend.user.response.UserTokenResponse.of(token, user.getRole());
    }

    // reissueToken은 API 모듈에서 구현

    // 로그아웃은 API 모듈에서 구현

    // 회원탈퇴 - 회원 관련 정보 삭제
    // 토큰 무효화 및 unlinking은 API 모듈에서 구현
    @Transactional
    public void withdraw(User user, String reason) {
        user.updateUserReasonOfWithdrawal(reason);
        userRepository.flush();
        userRepository.delete(user);
    }

    // 약관 동의 - 회원의 ROLE을 USER로 업데이트
    @Transactional
    public void agreements(bitnagil.bitnagil_backend.user.request.UserAgreementsRequest userAgreeMentsRequest, User user) {
        // 약관 동의 시 ROLE을 USER로 변경 및 동의 여부 업데이트
        User persistedUser = userManager.getPersistedUser(user);

        if(userAgreeMentsRequest.getAgreedToTermsOfService() == false ||
                userAgreeMentsRequest.getAgreedToPrivacyPolicy() == false ||
                userAgreeMentsRequest.getIsOverFourteen() == false) {
            throw new CustomException(ErrorCode.AGREEMENT_NOT_ACCEPTED);
        }

        persistedUser.updateAgreements(userAgreeMentsRequest.getAgreedToTermsOfService(),
                userAgreeMentsRequest.getAgreedToPrivacyPolicy(),
                userAgreeMentsRequest.getIsOverFourteen());
    }

    // 회원 온보딩 정보 조회
    @Transactional(readOnly = true)
    public UserOnboardingResponse getUserOnboarding(User user) {
        Onboarding onboarding = user.getOnboarding();

        return userMapper.toUserOnboardingResponse(
            onboarding.getTimeSlot(),
            onboarding.getEmotionType(),
            onboarding.getTargetOutingFrequency());
    }

    // 소셜 로그인 - 신규 유저는 DB 등록
    // userAuthInfo DTO 대신 도메인용 DTO 구성
    private User signUpOrLogin(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {

        return userRepository
                .findBySocialTypeAndSocialId(socialType, userAuthInfo.getSocialId())
                .orElseGet(() -> saveUser(socialType, nickname, userAuthInfo));
    }

    private User saveUser(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {
        LocalDateTime now = LocalDateTime.now();
        // 애플 로그인 시 닉네임은 클라이언트에서 보내준 값을 사용한다.
        nickname = (socialType == SocialType.APPLE) ? nickname : userAuthInfo.getNickname();

        User user = User.builder()
                .socialType(socialType)
                .socialId(userAuthInfo.getSocialId())
                .role(Role.GUEST) // 최초 가입 시 GUEST로 설정
                .email(userAuthInfo.getEmail())
                .nickname(nickname)
                .refreshToken(userAuthInfo.getRefreshToken()) // 애플 로그인의 경우만 세팅
                .build();

        return userRepository.save(user);
    }
}
