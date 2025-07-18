package bitnagil.bitnagil_backend.routine.domain;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.utils.DayOfWeekConverter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 루틴에 관련된 정보들을 관리하는 클래스입니다.
 *
 * Routine 테이블과 User 테이블은 N:1 관계입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Routine extends BaseTimeEntity {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "routine_id")),
        @AttributeOverride(name = "historySeq", column = @Column(name = "history_seq"))
    })
    private HistoryPk routinePk;

    @NotNull
    private String name;

    @NotNull
    @Convert(converter = DayOfWeekConverter.class)
    private List<DayOfWeek> repeatDay;

    @NotNull
    private LocalTime executionTime;

    @NotNull
    private LocalDateTime historyStartDateTime;

    @NotNull
    private LocalDateTime historyEndDateTime;

    @NotNull
    private UUID userId;

    @Builder
    public Routine(HistoryPk routinePk, String name, List<DayOfWeek> repeatDay, LocalTime executionTime,
        LocalDateTime historyStartDateTime, LocalDateTime historyEndDateTime, UUID userId) {
        this.routinePk = routinePk;
        this.name = name;
        this.repeatDay = repeatDay;
        this.executionTime = executionTime;
        this.historyStartDateTime = historyStartDateTime;
        this.historyEndDateTime = historyEndDateTime;
        this.userId = userId;
    }

    // 이전 루틴의 이력 종료일시를 갱신
    public void updateHistoryEndDateTime(LocalDateTime updateDateTime) {
        this.historyEndDateTime = updateDateTime;
    }
}
