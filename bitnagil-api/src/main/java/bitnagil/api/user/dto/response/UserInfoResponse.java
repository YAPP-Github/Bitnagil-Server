package bitnagil.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private String nickname;

    public static UserInfoResponse from(
        bitnagil.bitnagil_domain.user.dto.response.UserInfoResponse domainResponse) {
        return UserInfoResponse.builder()
            .nickname(domainResponse.getNickname())
            .build();
    }
}
