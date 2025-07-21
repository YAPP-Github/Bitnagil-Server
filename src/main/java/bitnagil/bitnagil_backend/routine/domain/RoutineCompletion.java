package bitnagil.bitnagil_backend.routine.domain;

import java.time.LocalDate;
import java.util.UUID;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 루틴 완료 여부를 관리하는 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoutineCompletion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineCompletionId;

    @NotNull
    private Boolean completeYn;

    @NotNull
    private LocalDate performedDate;

    @NotNull
    private UUID routineId;

    @NotNull
    private Long routineHistorySeq;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)", nullable = false)
    private RoutineType routineType;

    @Builder
    public RoutineCompletion(Boolean completeYn, LocalDate performedDate, UUID routineId, Long routineHistorySeq,
        RoutineType routineType) {
        this.completeYn = completeYn;
        this.performedDate = performedDate;
        this.routineId = routineId;
        this.routineHistorySeq = routineHistorySeq;
        this.routineType = routineType;
    }

    public void updateCompleteYn(Boolean completeYn) {
        this.completeYn = completeYn;
    }
}
