package bitnagil.bitnagil_backend.auth.kakao.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class KakaoFeignClientConfiguration {

    @Bean
    public ErrorDecoder kakaoFeignErrorDecoder() {
        return new KakaoFeignClientErrorHandler();
    }
}