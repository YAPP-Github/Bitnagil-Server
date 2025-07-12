package bitnagil.bitnagil_backend.routine.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import bitnagil.bitnagil_backend.global.utils.DayOfWeekConverter;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
 * 루틴에 관련된 정보들을 관리하는 클래스입니다.
 *
 * Routine 테이블과 User 테이블은 N:1 관계입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
    private LocalDateTime historyStartDate;

    @NotNull
    private LocalDateTime historyEndDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Builder
    public Routine(String name, List<DayOfWeek> repeatDay, LocalTime executionTime, LocalDateTime historyStartDate,
        LocalDateTime historyEndDate,
        User user) {
        this.name = name;
        this.repeatDay = repeatDay;
        this.executionTime = executionTime;
        this.historyStartDate = historyStartDate;
        this.historyEndDate = historyEndDate;
        this.user = user;
    }
}
