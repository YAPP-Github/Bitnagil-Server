package bitnagil.bitnagil_backend.routine.domain;


import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "subRoutine")
public class SubRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subRoutineId;

    @NotNull
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "routine_id")
    @NotNull
    private Routine routine;

    @Builder
    public SubRoutine(String name, LocalDate startDate, LocalDate endDate, Routine routine) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.routine = routine;
    }

    public static SubRoutine createSubRoutine(String name, Routine routine, LocalDate endDate) {
        return SubRoutine.builder()
            .name(name)
            .startDate(LocalDate.now())
            .endDate(endDate)
            .routine(routine)
            .build();
    }
}