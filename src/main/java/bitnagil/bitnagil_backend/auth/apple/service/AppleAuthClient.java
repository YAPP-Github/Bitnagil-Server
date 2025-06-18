package bitnagil.bitnagil_backend.auth.apple.service;

import bitnagil.bitnagil_backend.auth.apple.response.AppleSocialTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 애플 인증관련 Feign 클라이언트
 */
@Component
@FeignClient(
        name = "apple-auth",
        url = "${client.apple-auth.url}",
        configuration = AppleFeignClientConfiguration.class
)
public interface AppleAuthClient {

    // 애플 토큰 검증 API
    @PostMapping("/auth/token")
    AppleSocialTokenInfoResponse getIdToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code
    );
}