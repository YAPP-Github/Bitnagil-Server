package bitnagil.bitnagil_backend.user.service;

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
 *
 * 추후 Apple 로그인이 추가될 예정
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        Token token = jwtProvider.generateToken(user.getUserId());

        return TokenResponse.of(token);
    }

    // refreshToken으로 accessToken 재발행
    @Transactional
    public TokenResponse reissueToken(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Long userId = Long.valueOf(jwtProvider.parseClaims(refreshToken).get("userId", Integer.class));
        // 실제로 DB에 있는 userId 인지 검증
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        RefreshToken refreshTokenByRedis = authRedisService.getRefreshTokenByUserId(user.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_JWT_TOKEN));

        if(!refreshTokenByRedis.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Token token = jwtProvider.generateToken(userId);

        return TokenResponse.of(token);
    }

    // refreshToken 삭제 및 카카오 토큰 무효화
    @Transactional
    public void logout(User user) {
        invalidateToken(user);
        kakaoUserInfoService.logout(user);
    }

    // 회원탈퇴 - 회원 관련 정보 삭제 및 소셜과 연결 끊기
    @Transactional
    public void withdrawal(User user) {
        invalidateToken(user);

        userRepository.deleteById(user.getUserId());
        // TODO soft delete 범위에 대해 추후 논의 후 적용

        unlinkFromSocial(user);
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
        authRedisService.deleteRefreshToken(user.getUserId());

        // 서비스 액세스 토큰 블랙리스트 처리
        // String accessToken = jwtProvider.resolveToken(request);
        // Long expirationTime = jwtProvider.getExpirationTime(accessToken);
        // authRedisService.addAccessTokenToBlacklist(accessToken, expirationTime);
    }

    // 소셜 로그인 - 신규 유저는 DB 등록
    private User signUpOrLogin(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {
        return userRepository.findBySocialTypeAndSocialId(socialType, userAuthInfo.getSocialId())
            .orElseGet(() -> saveUser(socialType, nickname, userAuthInfo));
    }

    private User saveUser(SocialType socialType, String nickname, UserAuthInfo userAuthInfo) {
        // 애플 로그인 시 닉네임은 클라이언트에서 보내준 값을 사용한다.
        nickname = (socialType == SocialType.APPLE) ? nickname : userAuthInfo.getNickname();

        User user = User.builder()
            .socialType(socialType)
            .socialId(userAuthInfo.getSocialId())
            .role(Role.USER)
            .email(userAuthInfo.getEmail())
            .nickname(nickname)
            .refreshToken(userAuthInfo.getRefreshToken()) // 애플 로그인의 경우만 세팅
            .build();

        return userRepository.save(user);
    }
}