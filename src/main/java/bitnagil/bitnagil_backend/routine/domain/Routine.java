package bitnagil.bitnagil_backend.routine.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.utils.DayOfWeekConverter;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 루틴에 관련된 정보들을 관리하는 클래스입니다.
 *
 * Routine 테이블과 User 테이블은 N:1 관계입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE routine SET deleted_at = CURRENT_TIMESTAMP WHERE routine_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Routine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId; // 루틴 ID

    @NotNull
    private Long businessId; // 비즈니스 ID

    @NotNull
    private String name; // 루틴이름

    @NotNull
    @Convert(converter = DayOfWeekConverter.class)
    private List<DayOfWeek> repeatDay; // 루틴 반복요일

    @NotNull
    private LocalDate routineDate; // 루틴 일자

    @NotNull
    private LocalTime executionTime; // 루틴 실행시간

    @NotNull
    private LocalDate routineStartDate; // 루틴 시작일자

    @NotNull
    private LocalDate routineEndDate; // 루틴 종료일자

    @NotNull
    private boolean routineCompleteYn; // 루틴 완료여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Routine(Long routineId, Long businessId, String name, List<DayOfWeek> repeatDay,
                   LocalDate routineDate, LocalTime executionTime, LocalDate routineStartDate, LocalDate routineEndDate,
                   boolean routineCompleteYn, User user) {
        this.routineId = routineId;
        this.businessId = businessId;
        this.name = name;
        this.repeatDay = repeatDay;
        this.routineDate = routineDate;
        this.executionTime = executionTime;
        this.routineStartDate = routineStartDate;
        this.routineEndDate = routineEndDate;
        this.routineCompleteYn = routineCompleteYn;
        this.user = user;
    }

    // 서브루틴을 제외한 루틴 필드에서 변경된 필드가 있는지 검증
    public boolean hasRoutineChanged(UpdateRoutineRequest request) {
        return !this.getName().equals(request.getRoutineName()) ||
            !this.getRepeatDay().equals(request.getRepeatDay()) ||
            !this.getExecutionTime().equals(request.getExecutionTime());
    }
}
