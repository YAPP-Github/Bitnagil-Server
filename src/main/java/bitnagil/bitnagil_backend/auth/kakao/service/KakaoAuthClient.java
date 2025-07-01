package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;

/**
 * 카카오 서버와 통신하는 클래스입니다.
 */
@FeignClient(
    name = "KakaoAuthFeignClient",
    url = "${spring.security.oauth2.client.provider.kakao-provider.base-uri}",
    configuration = KakaoFeignClientConfiguration.class
)
public interface KakaoAuthClient {

    // 카카오 회원 정보 조회 API
    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorizationHeader);

    // 카카오 액세스 토큰 무효화 API
    @PostMapping("/v1/user/logout")
    String logout(
        @RequestHeader(value = "Authorization") String adminKey,
        @RequestHeader(value = "Content-Type") String contentType,
        @RequestParam("target_id_type") String targetIdType,
        @RequestParam("target_id") Long socialId);

    // 카카오 연동 해제 API (연결 끊기)
    @PostMapping("/v1/user/unlink")
    String unlink(
        @RequestHeader(value = "Authorization") String adminKey,
        @RequestHeader(value = "Content-Type") String contentType,
        @RequestParam("target_id_type") String targetIdType,
        @RequestParam("target_id") Long socialId
    );
}
