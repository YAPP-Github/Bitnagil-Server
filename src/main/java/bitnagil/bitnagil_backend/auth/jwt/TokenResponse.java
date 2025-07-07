package bitnagil.bitnagil_backend.auth.jwt;

import bitnagil.bitnagil_backend.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 *  로그인 후 토큰 관련 JSON 정보를 담은 클래스입니다.
 */
@Getter
@AllArgsConstructor
@Builder
public class TokenResponse {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

    @NotEmpty
    private Role role;

    public static TokenResponse of(Token token) {
        return TokenResponse.builder()
            .accessToken(token.getAccessToken())
            .refreshToken(token.getRefreshToken())
            .build();
    }

    public static TokenResponse of(Token token, Role role) {
        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .role(role)
                .build();
    }
}
