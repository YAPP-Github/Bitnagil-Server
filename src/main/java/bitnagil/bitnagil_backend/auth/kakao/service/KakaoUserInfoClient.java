package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;

@FeignClient(
    name = "kakaoUserInfoClient",
    url = "${spring.security.oauth2.client.provider.kakao-provider.base-uri}",
    configuration = KakaoFeignClientConfiguration.class
)
public interface KakaoUserInfoClient {
    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorizationHeader);
}