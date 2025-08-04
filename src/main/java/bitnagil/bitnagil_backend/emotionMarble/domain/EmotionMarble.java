package bitnagil.bitnagil_backend.emotionMarble.domain;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class EmotionMarble extends BaseTimeEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "emotion_marble_id")),
            @AttributeOverride(name = "historySeq", column = @Column(name = "history_seq"))
    })
    private HistoryPk emotionMarblePk;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private EmotionMarbleType emotionMarbleType;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long userId;

    @NotNull
    private LocalDateTime historyStartDateTime;

    @NotNull
    private LocalDateTime historyEndDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case resultCase;

    @Builder
    public EmotionMarble(HistoryPk emotionMarblePk, EmotionMarbleType emotionMarbleType, LocalDate date, Long userId,
                         LocalDateTime historyStartDateTime, LocalDateTime historyEndDateTime, Case resultCase) {
        this.emotionMarblePk = emotionMarblePk;
        this.emotionMarbleType = emotionMarbleType;
        this.date = date;
        this.userId = userId;
        this.historyStartDateTime = historyStartDateTime;
        this.historyEndDateTime = historyEndDateTime;
        this.resultCase = resultCase;
    }
}
