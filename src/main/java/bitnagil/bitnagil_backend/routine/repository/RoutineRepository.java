package bitnagil.bitnagil_backend.routine.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.routine.domain.Routine;

public interface RoutineRepository extends JpaRepository<Routine, HistoryPk> {

    Optional<Routine> findByRoutinePk(HistoryPk routinePk);

    // routine_id와 활성 구간(현재 시점) 조건을 모두 만족하는 루틴 조회
    Optional<Routine> findByRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
        UUID routineId, LocalDateTime historyStartDateBound, LocalDateTime historyEndDateBound);
}
