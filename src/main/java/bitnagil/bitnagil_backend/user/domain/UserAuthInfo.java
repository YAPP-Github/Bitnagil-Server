package bitnagil.bitnagil_backend.user.domain;

import bitnagil.bitnagil_backend.auth.kakao.response.KakaoUserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 인증 후 사용자 정보를 통합 관리하기 위한 DTO 클래스입니다.
 *
 * 다양한 소셜 로그인 서비스(카카오, 애플 등)에서 제공하는 사용자 정보를
 * 서비스 내부에서 일관된 형태로 사용하기 위해 표준화된 구조로 변환합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthInfo {
    private String socialId;  // 카카오, apple 사용자 고유 ID
    private String nickname;  // 카카오, apple 닉네임
    private String email;     // 카카오, apple 이메일

    // 카카오 응답 객체로부터 UserAuthInfo로 변환하는 정적 팩토리 메서드
    public static UserAuthInfo from(KakaoUserInfoResponse kakaoUserInfoResponse) {
        return UserAuthInfo.builder()
            .socialId(kakaoUserInfoResponse.getId())
            .nickname(kakaoUserInfoResponse.getKakaoAccount().getProfile().getNickname())
            .email(kakaoUserInfoResponse.getKakaoAccount().getEmail())
            .build();
    }

    // TODO 애플 응답 객체로부터 UserAuthInfo로 변환하는 정적 팩토리 메서드
}