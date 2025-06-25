package bitnagil.bitnagil_backend.auth.kakao.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kakao FeignClient 호출 시 예외를 처리하는 핸들러입니다.
 */
@Slf4j
@RequiredArgsConstructor
public class KakaoFeignClientErrorDecoder implements ErrorDecoder {

    /**
     * Feign Client 호출 시 HTTP 응답 코드가 300 이상인 경우 호출되는 메소드.
     * 응답 본문이 존재할 경우, ObjectMapper를 사용하여 디코딩을 시도하고 로그로 남깁니다.
     * 디코딩 실패 시에도 예외를 무시하고 로그만 남긴 후, 공통 CustomException을 반환합니다.
     * 모든 오류는 공통 에러 코드 (APPLE_FEIGN_CALL_FAILED)로 변환하여 상위 서비스에서 일관되게 처리할 수 있도록 합니다.
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        String body = null;
        if (response != null && response.body() != null) {
            try {
                body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Error reading response body", e);
            }
        }

        log.error("카카오 소셜 로그인 Feign API Feign Client 호출 중 오류가 발생되었습니다. body: {}", body);

        // 로그에서 어떤 카카오 요청이었는지 바로 트래킹하기 위한 예외 처리
        if (methodKey.contains("getUserInfo")) {
            return new CustomException(ErrorCode.KAKAO_USER_INFO_FAILED);
        } else if (methodKey.contains("logout")) {
            return new CustomException(ErrorCode.KAKAO_LOGOUT_FAILED);
        } else if (methodKey.contains("unlink")) {
            return new CustomException(ErrorCode.KAKAO_UNLINK_FAILED);
        }

        throw new CustomException(ErrorCode.KAKAO_FEIGN_CALL_FAILED);
    }
}
