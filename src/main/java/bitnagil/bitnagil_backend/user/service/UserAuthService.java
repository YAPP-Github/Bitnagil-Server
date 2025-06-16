package bitnagil.bitnagil_backend.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.auth.jwt.RefreshToken;
import bitnagil.bitnagil_backend.auth.jwt.Token;
import bitnagil.bitnagil_backend.auth.jwt.JwtProvider;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoClient;
import bitnagil.bitnagil_backend.auth.jwt.AuthRedisService;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.Repository.UserRepository;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.auth.jwt.TokenResponse;
import bitnagil.bitnagil_backend.user.entity.User;
import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoAccount;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
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
    private static final String AUTHORIZATION_TYPE = "Bearer ";

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final AuthRedisService authRedisService;

    @Transactional
    public TokenResponse socialLogin(SocialType socialType, String socialAccessToken) {
        if (socialType.equals(SocialType.KAKAO)) {

            return kakaoLogin(socialAccessToken);
        }

        // TODO 애플 로그인 추가
        return null;
    }

    private TokenResponse kakaoLogin(String kakaoAccessToken) {

        KakaoUserInfoResponse userInfo = kakaoUserInfoClient.getUserInfo(AUTHORIZATION_TYPE + kakaoAccessToken);

        User user = signUpOrLogin(SocialType.KAKAO, userInfo.getId(), userInfo.getKakaoAccount());

        Token token = jwtProvider.generateToken(user.getUserId());

        return TokenResponse.of(token);
    }

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

        // TODO 애플 로그인 토큰 재발행 로직 추가
    }

    private User signUpOrLogin(SocialType socialType, String socialId, KakaoAccount kakaoAccount) {
        return userRepository.findBySocialTypeAndSocialId(socialType, socialId)
            .orElseGet(() -> saveMember(socialType, socialId, kakaoAccount));
    }

    private User saveMember(SocialType socialType, String socialId, KakaoAccount kakaoAccount) {
        User user = User.builder()
            .socialType(socialType)
            .socialId(socialId)
            .role(Role.USER)
            .email(kakaoAccount.getEmail())
            .nickname(kakaoAccount.getProfile().getNickname())
            .build();

        return userRepository.save(user);
    }
}