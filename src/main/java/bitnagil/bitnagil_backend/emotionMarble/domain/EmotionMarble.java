package bitnagil.bitnagil_backend.emotionMarble.domain;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class EmotionMarble extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionMarbleId; // 감정구슬 ID

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private EmotionMarbleType emotionMarbleType; // 감정구슬 타입

    @NotNull
    private LocalDate date; // 감정구슬 선택 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case resultCase;

    @Builder
    public EmotionMarble(Long emotionMarbleId, EmotionMarbleType emotionMarbleType, LocalDate date, User user, Case resultCase) {
        this.emotionMarbleType = emotionMarbleType;
        this.date = date;
        this.user = user;
        this.resultCase = resultCase;
    }
}
