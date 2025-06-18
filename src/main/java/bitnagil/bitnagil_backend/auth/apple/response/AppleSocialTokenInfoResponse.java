package bitnagil.bitnagil_backend.auth.apple.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 애플 소셜 로그인 토큰 정보 응답 클래스
 * 애플 로그인 후 받은 토큰 정보를 매핑하는 클래스
 */
@Getter
public class AppleSocialTokenInfoResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("id_token")
    private String idToken;
}