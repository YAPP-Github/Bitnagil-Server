package bitnagil.bitnagil_backend.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.auth.apple.domain.AppleIdTokenPayload;
import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoClient;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserUnlinkClient;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.domain.UserAuthInfo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthHandler {
    private static final String KAKAO_AUTH_PREFIX = "KakaoAK ";
    private static final Integer KAKAO_UNAUTHORIZED_STATUS = 401;
    private static final Integer KAKAO_INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int MAX_RETRY_COUNT = 3;
    private static final long BASE_SLEEP_TIME_MS = 1000;

    private static final String AUTHORIZATION_TYPE = "Bearer ";

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String kakaoAdminKey;

    private final AppleUserInfoService appleUserInfoService;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final KakaoUserUnlinkClient kakaoUserUnlinkClient;


    // kakao, apple 서버에 회원 정보를 요청하고, UserAuthInfo에 매핑
    public UserAuthInfo getUserAuthInfo(SocialType socialType, String socialAccessToken) {
        return switch (socialType) {
            case KAKAO -> {
                KakaoUserInfoResponse kakaoUserInfoResponse = retryableGetKakaoUserInfo(socialAccessToken);
                yield UserAuthInfo.from(kakaoUserInfoResponse);
            }
            case APPLE -> {
                AppleIdTokenPayload appleIdTokenPayload = appleUserInfoService.get(socialAccessToken);
                yield UserAuthInfo.from(appleIdTokenPayload);
            }
        };
    }

    // 회원탈퇴를 위해 소셜과 연결을 끊는 외부 API
    public void unlinkFromSocial(User user) {
        switch (user.getSocialType()) {
            case KAKAO -> {
                String socialId = kakaoUserUnlinkClient.kakaoUnlink(
                    KAKAO_AUTH_PREFIX + kakaoAdminKey,
                    "application/x-www-form-urlencoded;charset=utf-8",
                    "user_id",
                    Long.valueOf(user.getSocialId())
                );
            }
            case APPLE -> {
                //TODO 애플과 연결끊기 로직 추가 예정
            }
        };
    }

    // 발생 가능한 에러에 대해 retry 처리한 카카오 회원 정보 API 조회
    private KakaoUserInfoResponse retryableGetKakaoUserInfo(String accessToken) {
        int retryCount = 0;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                return kakaoUserInfoClient.getUserInfo(AUTHORIZATION_TYPE + accessToken);
            } catch (FeignException e) {
                int status = e.status();
                if (status == KAKAO_INTERNAL_SERVER_ERROR_STATUS) { // 서버 오류: 재시도
                    retryCount++;
                    try {
                        Thread.sleep(BASE_SLEEP_TIME_MS * retryCount); // 점진적 재시도 간격
                    } catch (InterruptedException ie) { // 대기하는 과정에서 외부의 인터럽트 신호를 받을 경우 예외 처리
                        Thread.currentThread().interrupt();
                        throw new CustomException(ErrorCode.KAKAO_UNKNOWN_ERROR);
                    }
                } else if (status == KAKAO_UNAUTHORIZED_STATUS) {
                    throw new CustomException(ErrorCode.KAKAO_UNAUTHORIZED);
                } else {
                    throw new CustomException(ErrorCode.KAKAO_UNKNOWN_ERROR);
                }
            }
        }
        throw new CustomException(ErrorCode.KAKAO_UNKNOWN_ERROR); // 최대 재시도 초과 시 예외
    }
}
