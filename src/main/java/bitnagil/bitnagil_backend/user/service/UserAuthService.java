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
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
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
    private static final String AUTHORIZATION_TYPE = "Bearer ";

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final AuthRedisService authRedisService;

    // 소셜 로그인을 통해 로그인 혹은 회원가입을 진행
    @Transactional
    public TokenResponse socialLogin(SocialType socialType, String socialAccessToken) {

        UserAuthInfo userAuthInfo = getUserAuthInfo(socialType, socialAccessToken);

        User user = signUpOrLogin(socialType, userAuthInfo);

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
    }

    // kakao, apple 서버에 회원 정보를 요청하고, UserAuthInfo에 매핑
    private UserAuthInfo getUserAuthInfo(SocialType socialType, String socialAccessToken) {
        switch (socialType) {
            case KAKAO:
                KakaoUserInfoResponse kakaoUserInfoResponse = kakaoUserInfoClient.getUserInfo(
                    AUTHORIZATION_TYPE + socialAccessToken);

                return UserAuthInfo.from(kakaoUserInfoResponse);

            case APPLE:
                // TODO 애플 회원 정보 요청 API
                // TODO 애플 회원 정보를 UserAuthInfo에 매핑
                return null;

            default:
                throw new CustomException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
        }
    }

    private User signUpOrLogin(SocialType socialType, UserAuthInfo userAuthInfo) {
        return userRepository.findBySocialTypeAndSocialId(socialType, userAuthInfo.getSocialId())
            .orElseGet(() -> saveMember(socialType, userAuthInfo));
    }

    private User saveMember(SocialType socialType, UserAuthInfo userAuthInfo) {
        User user = User.builder()
            .socialType(socialType)
            .socialId(userAuthInfo.getSocialId())
            .role(Role.USER)
            .email(userAuthInfo.getEmail())
            .nickname(userAuthInfo.getNickname())
            .build();

        return userRepository.save(user);
    }
}