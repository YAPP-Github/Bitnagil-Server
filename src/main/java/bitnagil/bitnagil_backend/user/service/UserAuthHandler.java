package bitnagil.bitnagil_backend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.auth.apple.domain.AppleIdTokenPayload;
import bitnagil.bitnagil_backend.auth.apple.service.AppleUserInfoService;
import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoLogoutClient;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserInfoClient;
import bitnagil.bitnagil_backend.auth.kakao.service.KakaoUserUnlinkClient;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.domain.UserAuthInfo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

/**
 * 소셜 인증 API를 핸들링하는 클래스입니다.
 *
 * 카카오, 애플 서버에 API를 요청하는 로직들을 관리합니다.
 */
@Service
@RequiredArgsConstructor
public class UserAuthHandler {
    private static final String KAKAO_AUTH_PREFIX = "KakaoAK ";
    private static final Integer KAKAO_UNAUTHORIZED_STATUS = 401;
    private static final Integer KAKAO_INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int MAX_RETRY_COUNT = 3;
    private static final long BASE_SLEEP_TIME_MS = 1000;

    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private final KakaoLogoutClient kakaoLogoutClient;

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String kakaoAdminKey;

    private final AppleUserInfoService appleUserInfoService;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final KakaoUserUnlinkClient kakaoUserUnlinkClient;


    // kakao, apple 서버에 회원 정보를 요청하고, UserAuthInfo에 매핑
    public UserAuthInfo getUserAuthInfo(SocialType socialType, String socialAccessToken) {
        switch (socialType) {
            case KAKAO -> {
                int retryCount = 0;

                while (retryCount < MAX_RETRY_COUNT) {
                    try {
                        KakaoUserInfoResponse kakaoUserInfoResponse = getKakaoUserInfo(socialAccessToken);
                        return UserAuthInfo.from(kakaoUserInfoResponse);
                    } catch (FeignException e) {
                        retryCount = handleFeignException(e, retryCount);
                    }
                }
            }
            case APPLE -> {
                AppleIdTokenPayload appleIdTokenPayload = appleUserInfoService.get(socialAccessToken);
                return UserAuthInfo.from(appleIdTokenPayload);
            }
        };

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 회원탈퇴를 위해 소셜과 연결을 끊는 외부 API
    public void unlinkFromSocial(User user) {
        switch (user.getSocialType()) {
            case KAKAO -> {
                int retryCount = 0;

                while (retryCount < MAX_RETRY_COUNT) {
                    try {
                        String socialId = kakaoUserUnlinkClient.kakaoUnlink(
                            KAKAO_AUTH_PREFIX + kakaoAdminKey,
                            "application/x-www-form-urlencoded;charset=utf-8",
                            "user_id",
                            Long.valueOf(user.getSocialId())
                        );
                        break;
                    } catch (FeignException e) {
                        retryCount = handleFeignException(e, retryCount);
                    }
                }
            }
            case APPLE -> {
                // TODO 애플과 연결끊기 로직 추가 예정
            }
        };
    }

    // 카카오 accessToken 무효화
    public void invalidateAccessToken(User user, String accessToken) {
        switch (user.getSocialType()) {
            case KAKAO -> {
                int retryCount = 0;

                while (retryCount < MAX_RETRY_COUNT) {
                    try {
                        String socialId = kakaoLogoutClient.logout(
                            AUTHORIZATION_TYPE + accessToken,
                            "application/x-www-form-urlencoded;charset=utf-8"
                        );
                        break;
                    } catch (FeignException e) {
                        retryCount = handleFeignException(e, retryCount);
                    }
                }
            }

            case APPLE -> {
                // TODO 애플 액세스 토큰 무효화
            }
        }
    }

    // 발생 가능한 에러에 대해 retry 처리한 카카오 회원 정보 API 조회
    private KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        int retryCount = 0;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                return kakaoUserInfoClient.getUserInfo(AUTHORIZATION_TYPE + accessToken);
            } catch (FeignException e) {
                retryCount = handleFeignException(e, retryCount);
            }
        }
        throw new CustomException(ErrorCode.KAKAO_UNKNOWN_ERROR); // 최대 재시도 초과 시 예외
    }

    private int handleFeignException(FeignException e, int retryCount) {
        int status = e.status();

        if (status == KAKAO_INTERNAL_SERVER_ERROR_STATUS) { // 서버 오류: 재시도
            retryCount++;
            try {
                Thread.sleep(BASE_SLEEP_TIME_MS * retryCount);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorCode.KAKAO_RETRY_INTERRUPTED);
            }
            return retryCount;
        } else if (status == KAKAO_UNAUTHORIZED_STATUS) {
            throw new CustomException(ErrorCode.KAKAO_UNAUTHORIZED);
        } else {
            throw new CustomException(ErrorCode.KAKAO_UNKNOWN_ERROR);
        }
    }
}
