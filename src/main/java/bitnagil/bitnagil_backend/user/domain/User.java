package bitnagil.bitnagil_backend.user.domain;

import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 인증을 통해 가입한 사용자의 정보를 저장하는 JPA 엔티티 클래스입니다.
 * 소셜 타입, 소셜 ID, 이메일, 닉네임, 권한(Role) 등의 정보를 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String refreshToken; // 애플의 경우 탈퇴를 위한 필수값

    @Builder
    public User(SocialType socialType, String socialId, Role role, String email, String nickname, String refreshToken) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.refreshToken = refreshToken;
    }
}
