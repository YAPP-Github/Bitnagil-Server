package bitnagil.bitnagil_backend.user.response;

import bitnagil.bitnagil_backend.auth.jwt.Token;
import bitnagil.bitnagil_backend.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserReissueResponse {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

    public static UserReissueResponse of(Token token) {
        return UserReissueResponse.builder()
            .accessToken(token.getAccessToken())
            .refreshToken(token.getRefreshToken())
            .build();
    }
}
