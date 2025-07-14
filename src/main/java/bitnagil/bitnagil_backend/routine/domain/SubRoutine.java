package bitnagil.bitnagil_backend.routine.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;

import bitnagil.bitnagil_backend.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subRoutineId;

    @NotNull
    private String name;

    @NotNull
    private LocalDateTime historyStartDate;

    @NotNull
    private LocalDateTime historyEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    @NotNull
    private Routine routine;

    @Builder
    public SubRoutine(String name, LocalDateTime historyStartDate, LocalDateTime historyEndDate, Routine routine) {
        this.name = name;
        this.historyStartDate = historyStartDate;
        this.historyEndDate = historyEndDate;
        this.routine = routine;
    }

    public void updateHistory(LocalDateTime updateDateTime) {
        this.historyEndDate = updateDateTime;
    }
}