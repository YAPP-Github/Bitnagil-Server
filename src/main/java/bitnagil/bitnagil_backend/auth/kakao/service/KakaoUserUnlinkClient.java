package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
    name = "KakaoUserUnlinkClient",
    url = "${spring.security.oauth2.client.provider.kakao-provider.base-uri}",
    configuration = KakaoFeignClientConfiguration.class
)
public interface KakaoUserUnlinkClient {
    @PostMapping("/v1/user/unlink")
    String unlink(
        @RequestHeader(value = "Authorization") String adminKey,
        @RequestHeader(value = "Content-Type") String contentType,
        @RequestParam("target_id_type") String targetIdType,
        @RequestParam("target_id") Long socialId
    );
}
