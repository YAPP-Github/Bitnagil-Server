package bitnagil.bitnagil_backend.user.domain;

import bitnagil.bitnagil_backend.enums.Role;
import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.BaseTimeEntity;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import jakarta.persistence.*;
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
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private SocialType socialType;

    @Column(nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)", nullable = false)
    private Role role;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String refreshToken; // 애플의 경우 탈퇴를 위한 필수값

    private Boolean agreedToTermsOfService; // 서비스 이용약관 동의
    private Boolean agreedToPrivacyPolicy; // 개인정보 수집 동의
    private Boolean isOverFourteen; // 14세 이상 여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id")
    private Onboarding onboarding;

    @Builder
    public User(SocialType socialType, String socialId, Role role, String email, String nickname, String refreshToken,
                Boolean agreedToTermsOfService, Boolean agreedToPrivacyPolicy, Boolean isOverFourteen) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.refreshToken = refreshToken;
        this.agreedToTermsOfService = agreedToTermsOfService;
        this.agreedToPrivacyPolicy = agreedToPrivacyPolicy;
        this.isOverFourteen = isOverFourteen;
    }

    public void updateAgreements(Boolean agreedToTermsOfService, Boolean agreedToPrivacyPolicy, Boolean isOverFourteen) {
        this.agreedToTermsOfService = agreedToTermsOfService;
        this.agreedToPrivacyPolicy = agreedToPrivacyPolicy;
        this.isOverFourteen = isOverFourteen;
        this.role = Role.USER; // 약관 동의 후 권한을 USER로 변경
    }

    public void updateOnboarding(Onboarding onboarding) {
        this.onboarding = onboarding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // userId가 null이면 동등하지 않다고 판단
        return userId != null && userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
