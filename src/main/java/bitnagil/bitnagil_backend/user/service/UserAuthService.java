package bitnagil.bitnagil_backend.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.auth.jwt.RefreshToken;
import bitnagil.bitnagil_backend.auth.jwt.Token;
import bitnagil.bitnagil_backend.auth.jwt.JwtProvider;
import bitnagil.bitnagil_backend.auth.jwt.AuthRedisService;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoService;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.Repository.UserRepository;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.user.domain.UserAuthInfo;
import jakarta.servlet.http.HttpServletRequest;
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
    private final KakaoUserInfoService kakaoUserInfoService;

    // 소셜 로그인을 통해 로그인 혹은 회원가입을 진행
    @Transactional
    public TokenResponse socialLogin(SocialType socialType, String nickname, String socialAccessToken) {

        UserAuthInfo userAuthInfo = kakaoUserInfoService.getUserAuthInfo(socialType, socialAccessToken);

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

    // 로그아웃 - refreshToken 삭제 및 accessToken 블랙리스트 등록
    @Transactional
    public void logout(User user, HttpServletRequest request, String socialAccessToken) {
        invalidateToken(user, request);
        kakaoUserInfoService.invalidateKakaoToken(user, socialAccessToken);
    }



    // 회원탈퇴 - 회원 관련 정보 삭제 및 소셜과 연결 끊기
    @Transactional
    public void withdrawal(User user, HttpServletRequest request, String socialAccessToken) {
        invalidateToken(user, request);

        userRepository.deleteById(user.getUserId());
        // TODO soft delete 범위에 대해 추후 논의 후 적용

        kakaoUserInfoService.unlinkFromSocial(user);
    }

    // 서비스 accessToken, refreshToken 무효화
    private void invalidateToken(User user, HttpServletRequest request) {
        authRedisService.deleteRefreshToken(user.getUserId());

        String accessToken = jwtProvider.resolveToken(request);
        Long expirationTime = jwtProvider.getExpirationTime(accessToken);

        authRedisService.addAccessTokenToBlacklist(accessToken, expirationTime);
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
            .build();

        return userRepository.save(user);
    }
}