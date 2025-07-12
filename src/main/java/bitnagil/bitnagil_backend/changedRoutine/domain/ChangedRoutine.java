package bitnagil.bitnagil_backend.changedRoutine.domain;

import bitnagil.bitnagil_backend.global.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.utils.DayOfWeekConverter;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 규칙적인 류틴에 대해 일시적인 변경이 발생한 루틴에 대해 관리하는 엔티티입니다.
 * 시간 변경, 내일 미루기, 오늘만 루틴 삭제 등 기존 루틴을 수정,삭제하는 것이 아닌 일시적인 변경시에 해당 엔티티에 이력을 쌓습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChangedRoutine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changedRoutineId;

    @NotNull
    private String changedRoutineName; // 변경된 루틴 이름

    @NotNull
    private LocalTime changedExecutionTime; // 변경된 루틴 실행 시간

    @NotNull
    private LocalDate originalRoutineDate; // 원본 루틴 날짜(원본 루틴에서 해당 날짜는 루틴이 뜨지 않도록 한다.)

    @NotNull
    private LocalDate changedRoutineDate; // 변경된 루틴 날짜(실제 루틴이 실행될 날짜)

    @NotNull
    private LocalDateTime historyStartDate; // 이력 시작일시

    @NotNull
    private LocalDateTime historyEndDate; // 이력 종료일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 원본 루틴

    @Builder
    public ChangedRoutine(String changedRoutineName, LocalTime changedExecutionTime, LocalDate originalRoutineDate,
                          LocalDate changedRoutineDate, LocalDateTime historyStartDate, LocalDateTime historyEndDate,
                          User user, Routine routine) {
        this.changedRoutineName = changedRoutineName;
        this.changedExecutionTime = changedExecutionTime;
        this.originalRoutineDate = originalRoutineDate;
        this.changedRoutineDate = changedRoutineDate;
        this.historyStartDate = historyStartDate;
        this.historyEndDate = historyEndDate;
        this.user = user;
        this.routine = routine;
    }

}
