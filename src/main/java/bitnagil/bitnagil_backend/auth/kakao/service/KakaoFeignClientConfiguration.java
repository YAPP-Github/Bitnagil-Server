package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

/**
 * KakaoFeignClient에서 발생하는 예외를 핸들링 하기 위한 설정 클래스
 */
@Configuration
public class KakaoFeignClientConfiguration {

    @Bean
    public ErrorDecoder kakaoFeignErrorDecoder() {
        return new KakaoFeignClientErrorDecoder();
    }
}