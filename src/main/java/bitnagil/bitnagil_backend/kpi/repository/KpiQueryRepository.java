package bitnagil.bitnagil_backend.kpi.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * KPI 집계 전용 조회 쿼리를 모아둔 리포지토리 인터페이스입니다.
 * 복합 집계(루틴 완료 유저 수, 7일 내 완료 유저 수 등)를 제공합니다.
 */
public interface KpiQueryRepository {

    long countDistinctUsersWithRoutineCompletionV1InPeriod(LocalDate start, LocalDate end);

    long countDistinctUsersWithRoutineCompletionV2InPeriod(LocalDate start, LocalDate end);

    /**
     * 전월 기간 내 V1 또는 V2 루틴 1회 이상 완료한 distinct user_id 수 (중복 제거)
     */
    long countDistinctUsersWithRoutineCompletionInPeriod(LocalDate start, LocalDate end);


    long countDistinctUsersWithRoutineRegistrationInPeriod(LocalDateTime monthStart, LocalDateTime monthEnd);

    long countDistinctUsersWithOutingCompletionInPeriod(LocalDate start, LocalDate end);

    long countDistinctUsersWithEmotionInPeriod(LocalDate start, LocalDate end);

    List<Long> findDistinctActiveUserIdsInPeriod(
        LocalDate start,
        LocalDate end,
        LocalDateTime monthStart,
        LocalDateTime monthEnd);

    /**
     * 전월 신규 가입자 중 가입 후 7일 이내 V1 루틴 1회 이상 완료한 user_id 목록 (중복 제거는 서비스에서 V2와 합쳐서 수행)
     */
    List<Long> findUserIdsWithRoutineCompletionWithin7DaysV1(LocalDateTime monthStart, LocalDateTime monthEnd);

    /**
     * 전월 신규 가입자 중 가입 후 7일 이내 V2 루틴 1회 이상 완료한 user_id 목록
     */
    List<Long> findUserIdsWithRoutineCompletionWithin7DaysV2(LocalDateTime monthStart, LocalDateTime monthEnd);
}
