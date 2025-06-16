package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;

@FeignClient(
    name = "kakaoUserInfoClient",
    url = "${spring.security.oauth2.client.provider.kakao-provider.user-info-uri}"
)
public interface KakaoUserInfoClient {
    @GetMapping
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorizationHeader);
}