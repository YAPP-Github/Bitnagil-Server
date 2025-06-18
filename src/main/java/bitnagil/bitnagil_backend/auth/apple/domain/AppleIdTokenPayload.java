package bitnagil.bitnagil_backend.auth.apple.domain;

import lombok.Getter;

/**
 * 애플 ID 토큰 페이로드 클래스
 * 애플 로그인 후 받은 ID 토큰의 페이로드를 매핑하는 클래스
 */
@Getter
public class AppleIdTokenPayload {

    private String sub;

    private String email;

    private String name;
}