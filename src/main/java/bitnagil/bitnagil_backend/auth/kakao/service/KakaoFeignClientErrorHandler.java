package bitnagil.bitnagil_backend.auth.kakao.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KakaoFeignClientErrorHandler implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = null;
        if (response != null && response.body() != null) {
            try (InputStream is = response.body().asInputStream()) {
                body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Error reading response body", e);
            }
        }

        log.error("카카오 소셜 로그인 Feign API Feign Client 호출 중 오류가 발생되었습니다. body: {}", body);

        if (methodKey.contains("getUserInfo")) {
            // 회원정보 조회 API 예외 처리
            return new CustomException(ErrorCode.KAKAO_USER_INFO_FAILED);
        } else if (methodKey.contains("logout")) {
            // 로그아웃 API 예외 처리
            return new CustomException(ErrorCode.KAKAO_LOGOUT_FAILED);
        } else if (methodKey.contains("unlink")) {
            // 회원탈퇴(연결끊기) API 예외 처리
            return new CustomException(ErrorCode.KAKAO_UNLINK_FAILED);
        }

        throw new CustomException(ErrorCode.KAKAO_FEIGN_CALL_FAILED);
    }
}
