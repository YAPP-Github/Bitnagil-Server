package bitnagil.bitnagil_backend.changedRoutine.domain;

import bitnagil.bitnagil_backend.global.BaseTimeEntity;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChangedSubRoutine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changedSubRoutineId;

    @NotNull
    private String changedSubRoutineName;

    @NotNull
    private LocalDateTime historyStartDate;

    @NotNull
    private LocalDateTime historyEndDate;

    @ManyToOne
    @JoinColumn(name = "changed_routine_id")
    @NotNull
    private ChangedRoutine changedRoutine;

    @Builder
    public ChangedSubRoutine(String changedSubRoutineName, LocalDateTime historyStartDate, LocalDateTime historyEndDate,
                             ChangedRoutine changedRoutine) {
        this.changedSubRoutineName = changedSubRoutineName;
        this.historyStartDate = historyStartDate;
        this.historyEndDate = historyEndDate;
        this.changedRoutine = changedRoutine;
    }
}
