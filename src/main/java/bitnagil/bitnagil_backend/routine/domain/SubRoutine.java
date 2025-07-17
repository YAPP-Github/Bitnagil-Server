package bitnagil.bitnagil_backend.routine.domain;


import java.time.LocalDateTime;
import java.util.UUID;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 세부 루틴을 관리하는 엔티티 클래스입니다.
 *
 * SubRoutine 테이블과 Routine 테이블은 N:1 관계입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SubRoutine extends BaseTimeEntity {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "sub_routine_id")),
        @AttributeOverride(name = "historySeq", column = @Column(name = "history_seq"))
    })
    private HistoryPk subRoutinePk;

    @NotNull
    private String name;

    @NotNull
    private Integer sortOrder;

    @NotNull
    private LocalDateTime historyStartDateTime;

    @NotNull
    private LocalDateTime historyEndDateTime;

    @NotNull
    private UUID routineId;

    @Builder
    public SubRoutine(HistoryPk subRoutinePk, String name, Integer sortOrder, LocalDateTime historyStartDateTime,
        LocalDateTime historyEndDateTime, UUID routineId) {
        this.subRoutinePk = subRoutinePk;
        this.name = name;
        this.sortOrder = sortOrder;
        this.historyStartDateTime = historyStartDateTime;
        this.historyEndDateTime = historyEndDateTime;
        this.routineId = routineId;
    }

    // 이전 서브루틴의 이력 종료일시 갱신
    public void updateHistoryEndDateTime(LocalDateTime updateDateTime) {
        this.historyEndDateTime = updateDateTime;
    }

    // 서브루틴 순서 갱신
    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}