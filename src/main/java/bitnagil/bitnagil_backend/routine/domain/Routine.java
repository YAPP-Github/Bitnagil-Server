package bitnagil.bitnagil_backend.routine.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import bitnagil.bitnagil_backend.enums.DayOfWeek;
import bitnagil.bitnagil_backend.routine.request.RoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.Convert;
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
 * 루틴에 관련된 정보들을 관리하는 클래스입니다.
 *
 * Routine 테이블과 User 테이블은 N:1 관계입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "routine")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @NotNull
    private String name;

    @NotNull
    @Convert(converter = DayOfWeekConverter.class)
    private List<DayOfWeek> repeatDay;

    @NotNull
    private LocalTime executionTime;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Builder
    public Routine(String name, List<DayOfWeek> repeatDay, LocalTime executionTime, LocalDate startDate,
        LocalDate endDate,
        User user) {
        this.name = name;
        this.repeatDay = repeatDay;
        this.executionTime = executionTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    public static Routine createRoutine(User user, RoutineRequest request, LocalDate endDate) {
        return Routine.builder()
            .name(request.getRoutineName())
            .repeatDay(request.getDaysOfWeek())
            .executionTime(request.getExecutionTime())
            .startDate(LocalDate.now())
            .endDate(endDate)
            .user(user)
            .build();
    }
}
