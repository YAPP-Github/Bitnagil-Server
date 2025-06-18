package bitnagil.bitnagil_backend.auth.apple.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * AppleFeignClient에서 발생하는 예외를 핸들링 하기 위한 설정 클래스
 */
public class AppleFeignClientConfiguration {

    @Bean
    public AppleFeignClientErrorDecoder appleFeignClientErrorDecoder() {
        return new AppleFeignClientErrorDecoder(new ObjectMapper());
    }
}
