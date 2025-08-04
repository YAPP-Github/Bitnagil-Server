package bitnagil.bitnagil_backend.routineV2.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import bitnagil.bitnagil_backend.global.utils.DayOfWeekConverter;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.Convert;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoutineV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @NotNull
    private String routineName;

    @NotNull
    @Convert(converter = DayOfWeekConverter.class)
    private List<DayOfWeek> routineRepeatDay;

    @NotNull
    private LocalTime routineExecutionTime;

    @NotNull
    private LocalDate routineStartDate; // 루틴 시작일자

    @NotNull
    private LocalDate routineEndDate; // 루틴 종료일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RoutineV2(String routineName, List<DayOfWeek> routineRepeatDay, LocalTime routineExecutionTime,
        LocalDate routineStartDate, LocalDate routineEndDate, User user) {
        this.routineName = routineName;
        this.routineRepeatDay = routineRepeatDay;
        this.routineExecutionTime = routineExecutionTime;
        this.routineStartDate = routineStartDate;
        this.routineEndDate = routineEndDate;
        this.user = user;
    }
}
