package bitnagil.bitnagil_backend.auth.apple.service;

import bitnagil.common.errorcode.ErrorCode;
import bitnagil.common.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Feign 클라이언트 호출 중 오류가 발생했을 때 예외를 변환하는 ErrorDecoder 구현
 */
@Slf4j
@RequiredArgsConstructor
public class AppleFeignClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    /**
     * Feign Client 호출 시 HTTP 응답 코드가 300 이상인 경우 호출되는 메소드.
     * 응답 본문이 존재할 경우, ObjectMapper를 사용하여 디코딩을 시도하고 로그로 남깁니다.
     * 디코딩 실패 시에도 예외를 무시하고 로그만 남긴 후, 공통 CustomException을 반환합니다.
     * 모든 오류는 공통 에러 코드 (APPLE_FEIGN_CALL_FAILED)로 변환하여 상위 서비스에서 일관되게 처리할 수 있도록 합니다.
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        String body = null;
        try{
            if (response != null && response.body() != null) {
                body = new String(response.body().asInputStream().readAllBytes());

                // JSON 여부를 간단히 판단
                if (body.trim().startsWith("{")) {
                    // JSON 형식이면 파싱
                    try {
                        Object parsed = objectMapper.readValue(body, Object.class);
                        log.error("Feign 오류 응답 (JSON): {}", parsed);
                    } catch (IOException jsonEx) {
                        log.warn("JSON 파싱 실패: 원문 출력 -> {}", body, jsonEx);
                    }
                } else {
                    // JSON이 아니면 그냥 텍스트로 출력
                    log.error("Feign 오류 응답 (TEXT): {}", body);
                }
            }
        } catch (IOException e) {
            log.error("Feign 응답 바디 읽기 실패", e);
        }

        log.error("애플 소셜 로그인 Feign API Feign Client 호출 중 오류가 발생되었습니다. body: {}", body);
        return new CustomException(ErrorCode.APPLE_FEIGN_CALL_FAILED);
    }
}
