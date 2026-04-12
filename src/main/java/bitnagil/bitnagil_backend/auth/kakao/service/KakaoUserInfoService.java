package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import bitnagil.bitnagil_domain.user.domain.User;
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

    private final KakaoAuthClient kakaoAuthClient;

    // 클라이언트에서 받은 카카오 액세스 토큰으로 카카오 회원 정보를 조회
    public KakaoUserInfoResponse get(String accessToken) {
        return kakaoAuthClient.getUserInfo(AUTHORIZATION_TYPE + accessToken);
    }

    // 유저의 소셜 아이디로 카카오와 연동을 해제
    public void unlink(User user) {
        String socialId = kakaoAuthClient.unlink(
            KAKAO_AUTH_PREFIX + kakaoAdminKey,
            "application/x-www-form-urlencoded;charset=utf-8",
            "user_id",
            Long.valueOf(user.getSocialId())
        );
    }

    // 클라이언트에서 받은 카카오 액세스 토큰으로 카카오 액세스 토큰 무효화
    public void logout(User user) {
        String socialId = kakaoAuthClient.logout(
            KAKAO_AUTH_PREFIX + kakaoAdminKey,
            "application/x-www-form-urlencoded;charset=utf-8",
            "user_id",
            Long.valueOf(user.getSocialId())
        );
    }
}
