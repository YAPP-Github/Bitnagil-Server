package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.auth.apple.domain.AppleIdTokenPayload;
import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.domain.UserAuthInfo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

/**
 * 카카오 서버에 API를 요청을 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class KakaoUserInfoService {
    private static final String KAKAO_AUTH_PREFIX = "KakaoAK ";
    private static final String AUTHORIZATION_TYPE = "Bearer ";

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String kakaoAdminKey;

    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final KakaoUserUnlinkClient kakaoUserUnlinkClient;
    private final KakaoLogoutClient kakaoLogoutClient;

    // 클라이언트에서 받은 카카오 액세스 토큰으로 카카오 회원 정보를 조회
    public KakaoUserInfoResponse get(String accessToken) {
        return kakaoUserInfoClient.getUserInfo(AUTHORIZATION_TYPE + accessToken);
    }

    // 유저의 소셜 아이디로 카카오와 연동을 해제
    public void unlink(User user) {
        String socialId = kakaoUserUnlinkClient.unlink(
            KAKAO_AUTH_PREFIX + kakaoAdminKey,
            "application/x-www-form-urlencoded;charset=utf-8",
            "user_id",
            Long.valueOf(user.getSocialId())
        );
    }

    // 클라이언트에서 받은 카카오 액세스 토큰으로 카카오 액세스 토큰 무효화
    public void logout(String accessToken) {
        String socialId = kakaoLogoutClient.logout(
            AUTHORIZATION_TYPE + accessToken,
            "application/x-www-form-urlencoded;charset=utf-8"
        );
    }
}
