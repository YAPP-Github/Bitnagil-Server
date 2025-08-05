package bitnagil.bitnagil_backend.routineInfoV2.domain;

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

/**
 * 추후 v2를 대비한 루틴에 대한 정보를 관리하는 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoutineInfoV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineInfoId; // 루틴 정보 ID

    @NotNull
    private String routineName; // 루틴 이름

    @NotNull
    @Convert(converter = DayOfWeekConverter.class)
    private List<DayOfWeek> routineRepeatDay; // 루틴 반복 요일

    @NotNull
    private LocalTime routineExecutionTime; // 루틴 실행 시간

    @NotNull
    private LocalDate routineStartDate; // 루틴 시작 일자

    @NotNull
    private LocalDate routineEndDate; // 루틴 종료 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 루틴의 주체인 유저

    @Builder
    public RoutineInfoV2(String routineName, List<DayOfWeek> routineRepeatDay, LocalTime routineExecutionTime,
        LocalDate routineStartDate, LocalDate routineEndDate, User user) {
        this.routineName = routineName;
        this.routineRepeatDay = routineRepeatDay;
        this.routineExecutionTime = routineExecutionTime;
        this.routineStartDate = routineStartDate;
        this.routineEndDate = routineEndDate;
        this.user = user;
    }
}
