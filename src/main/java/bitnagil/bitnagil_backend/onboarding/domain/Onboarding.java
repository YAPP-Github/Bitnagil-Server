package bitnagil.bitnagil_backend.onboarding.domain;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.onboarding.domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Onboarding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long onboardingId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)") // mysql의 enum 타입을 사용하지 않도록 설정
    @NotNull
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    @NotNull
    private EmotionType emotionType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    @NotNull
    private RealOutingFrequency realOutingFrequency;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    @NotNull
    private TargetOutingFrequency targetOutingFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case resultCase;
}
