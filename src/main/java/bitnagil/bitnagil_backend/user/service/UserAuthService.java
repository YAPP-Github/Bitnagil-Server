package bitnagil.bitnagil_backend.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.user.request.UserAgreementsRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.auth.apple.domain.AppleIdTokenPayload;
import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.jwt.RefreshToken;
import bitnagil.bitnagil_backend.auth.jwt.Token;
import bitnagil.bitnagil_backend.auth.jwt.JwtProvider;
import bitnagil.bitnagil_backend.auth.jwt.AuthRedisService;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoService;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.user.domain.UserAuthInfo;
import lombok.RequiredArgsConstructor;

/**
 * 소셜 로그인 인증을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthRedisService authRedisService;
    private final AppleUserInfoService appleUserInfoService;
    private final KakaoUserInfoService kakaoUserInfoService;

    // 소셜 로그인을 통해 로그인 혹은 회원가입을 진행
    @Transactional
    public TokenResponse socialLogin(SocialType socialType, String nickname, String socialAccessToken) {

        UserAuthInfo userAuthInfo = getUserAuthInfo(socialType, socialAccessToken);

        User user = signUpOrLogin(socialType, nickname, userAuthInfo);

        Token token = jwtProvider.generateToken(user.getUserPk());

        return TokenResponse.of(token, user.getRole());
    }

    // refreshToken으로 accessToken 재발행
    @Transactional
    public TokenResponse reissueToken(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        User user = jwtProvider.findValidUserByRefreshTokenOrAccessToken(refreshToken);

        RefreshToken refreshTokenByRedis = authRedisService.getRefreshTokenByUserPk(user.getUserPk())
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_JWT_TOKEN));

        if(!refreshTokenByRedis.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Token token = jwtProvider.generateToken(user.getUserPk());

        return TokenResponse.of(token);
    }

    // refreshToken 삭제 및 카카오 토큰 무효화
    @Transactional
    public void logout(User user) {
        invalidateToken(user);
        // 카카오의 경우에만 카카오 인증 서버로 로그아웃 요청
        if(user.getSocialType() == SocialType.KAKAO) {
            kakaoUserInfoService.logout(user);
        }
    }

    // 회원탈퇴 - 회원 관련 정보 삭제 및 소셜과 연결 끊기
    @Transactional
    public void withdrawal(User user) {
        LocalDateTime now = LocalDateTime.now();

        invalidateToken(user);

        // 기존 유저의 이력 종료일시를 갱신
        user.updateHistoryEndDateTime(now);

        unlinkFromSocial(user);
    }

    // 약관 동의 - 회원의 ROLE을 USER로 업데이트
    @Transactional
    public void agreements(UserAgreementsRequest userAgreeMentsRequest, User user) {
        // 약관 동의 시 ROLE을 USER로 변경 및 동의 여부 업데이트
        User findUser = userRepository.findByUserPk(user.getUserPk()).orElseThrow(() ->
            new CustomException(ErrorCode.NOT_FOUND_USER));

        if(userAgreeMentsRequest.getAgreedToTermsOfService() == false ||
            userAgreeMentsRequest.getAgreedToPrivacyPolicy() == false ||
            userAgreeMentsRequest.getIsOverFourteen() == false) {
            throw new CustomException(ErrorCode.AGREEMENT_NOT_ACCEPTED);
        }

        findUser.updateAgreements(userAgreeMentsRequest.getAgreedToTermsOfService(),
                                  userAgreeMentsRequest.getAgreedToPrivacyPolicy(),
                                  userAgreeMentsRequest.getIsOverFourteen());
    }

    // kakao, apple 서버에 회원 정보를 요청하고, UserAuthInfo에 매핑
    private UserAuthInfo getUserAuthInfo(SocialType socialType, String socialAccessToken) {
        switch (socialType) {
            case KAKAO -> {
                KakaoUserInfoResponse kakaoUserInfoResponse = kakaoUserInfoService.get(socialAccessToken);
                return UserAuthInfo.from(kakaoUserInfoResponse);
            }
            case APPLE -> {
                AppleIdTokenPayload appleIdTokenPayload = appleUserInfoService.get(socialAccessToken);
                // 탈퇴 후 재가입 시 애플에서 탈퇴 처리가 늦어지는 경우 email 값이 null로 오는 문제를 방지하기 위한 예외 처리
                if (appleIdTokenPayload.getEmail() == null){
                    throw new CustomException(ErrorCode.APPLE_UNLINK_PENDING);
                }
                return UserAuthInfo.from(appleIdTokenPayload);
            }
        };

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 회원탈퇴를 위해 소셜과 연결을 끊는 외부 API
    private void unlinkFromSocial(User user) {
        switch (user.getSocialType()) {
            case KAKAO -> {
                kakaoUserInfoService.unlink(user);
            }
            case APPLE -> {
                appleUserInfoService.unlink(user);
            }
        };
    }

    // 서비스 refreshToken 무효화
    private void invalidateToken(User user) {
        authRedisService.deleteRefreshToken(user.getUserPk());

        // 서비스 액세스 토큰 블랙리스트 처리
        // String accessToken = jwtProvider.resolveToken(request);
        // Long expirationTime = jwtProvider.getExpirationTime(accessToken);
        // authRedisService.addAccessTokenToBlacklist(accessToken, expirationTime);
    }

    // 소셜 로그인 - 신규 유저는 DB 등록
    private User signUpOrLogin(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {
        LocalDateTime now = LocalDateTime.now();

        return userRepository
            .findBySocialTypeAndSocialIdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
            socialType, userAuthInfo.getSocialId(), now, now)
            .orElseGet(() -> saveUser(socialType, nickname, userAuthInfo));
    }

    private User saveUser(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {
        LocalDateTime now = LocalDateTime.now();
        // 애플 로그인 시 닉네임은 클라이언트에서 보내준 값을 사용한다.
        nickname = (socialType == SocialType.APPLE) ? nickname : userAuthInfo.getNickname();

        User user = User.builder()
            .userPk(new HistoryPk(UUID.randomUUID(), 1L))
            .socialType(socialType)
            .socialId(userAuthInfo.getSocialId())
            .role(Role.GUEST) // 최초 가입 시 GUEST로 설정
            .email(userAuthInfo.getEmail())
            .nickname(nickname)
            .refreshToken(userAuthInfo.getRefreshToken()) // 애플 로그인의 경우만 세팅
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .build();

        return userRepository.save(user);
    }
}