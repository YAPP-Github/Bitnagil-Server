package bitnagil.bitnagil_backend.routine.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import bitnagil.bitnagil_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.common.entity.HistoryPk;
import bitnagil.bitnagil_backend.routine.domain.Routine;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, HistoryPk> {

    Optional<Routine> findByRoutinePk(HistoryPk routinePk);
    List<Routine> findByRoutinePk_Id(UUID routinePkId);

    // routine_id와 활성 구간(현재 시점) 조건을 모두 만족하는 루틴 조회
    Optional<Routine> findByRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
        UUID routineId, LocalDateTime historyStartDateBound, LocalDateTime historyEndDateBound);
    boolean existsByName(String name);

    /**
     * 현재 시점을 기준으로 유저의 살아있는 루틴 이력을 조회
     * historyStartDate < systime <= historyEndDate
     */
    List<Routine> findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
            Long userId,
            LocalDateTime now1,
            LocalDateTime now2
    );

    List<Routine> findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeLessThanEqualAndHistoryEndDateTimeGreaterThanEqual(
            Long userId,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );
}
