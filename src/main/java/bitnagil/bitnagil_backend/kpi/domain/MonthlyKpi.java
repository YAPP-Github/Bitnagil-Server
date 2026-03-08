package bitnagil.bitnagil_backend.kpi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 월별 성과 KPI 집계 결과를 저장하는 엔티티입니다.
 * 매월 1일 스케줄러가 전월 데이터를 집계하여 한 건 저장합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MonthlyKpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 집계 대상 월 (해당 월 1일)
     */
    @NotNull
    @Column(name = "target_month", unique = true, nullable = false)
    private LocalDate targetMonth;

    /**
     * 지표 1: 가입 후 7일 이내 루틴 1회 이상 완료 비율 (%)
     * (7일 내 루틴 1회 이상 완료 유저 수 / 신규 가입자 수) × 100
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal routineCompletionWithinSevenDaysRate;

    /**
     * 지표 2: 월간 1회 이상 루틴 완료 사용자 비율 (%)
     * (월 1회 이상 루틴 완료 유저 수 / 전체 가입자 수) × 100
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal monthlyRoutineActiveUserRate;

    /**
     * 지표 3: 앱 접속 후 루틴 1개 이상 등록 비율 (%)
     * (루틴 1개 이상 등록 유저 수 / 월간 활성 사용자 수) × 100
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal routineRegistrationRate;

    /**
     * 지표 4: '나가봐요' 카테고리 루틴 1회 이상 완료 비율 (%)
     * (외출 루틴 1회 이상 완료 유저 수 / 월간 활성 사용자 수) × 100
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal outingRoutineCompletionRate;

    /**
     * 지표 5: 긍정 감정 비율 (%)
     * (긍정 감정 기록 수 / 전체 감정 기록 수) × 100 — 활기, 만족, 평온
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal positiveEmotionRate;

    @Builder
    public MonthlyKpi(LocalDate targetMonth,
        BigDecimal routineCompletionWithinSevenDaysRate,
        BigDecimal monthlyRoutineActiveUserRate,
        BigDecimal routineRegistrationRate,
        BigDecimal outingRoutineCompletionRate,
        BigDecimal positiveEmotionRate) {
        this.targetMonth = targetMonth;
        this.routineCompletionWithinSevenDaysRate = routineCompletionWithinSevenDaysRate;
        this.monthlyRoutineActiveUserRate = monthlyRoutineActiveUserRate;
        this.routineRegistrationRate = routineRegistrationRate;
        this.outingRoutineCompletionRate = outingRoutineCompletionRate;
        this.positiveEmotionRate = positiveEmotionRate;
    }
}
