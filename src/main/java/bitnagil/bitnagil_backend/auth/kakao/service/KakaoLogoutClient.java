package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "KakaoLogoutClient",
    url = "${spring.security.oauth2.client.provider.kakao-provider.base-uri}"
)
public interface KakaoLogoutClient {
    @PostMapping("/v1/user/logout")
    String logout(
        @RequestHeader("Authorization") String accessToken,
        @RequestHeader("Content-Type") String contentType);
}
