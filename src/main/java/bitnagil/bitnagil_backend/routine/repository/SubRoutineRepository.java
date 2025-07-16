package bitnagil.bitnagil_backend.routine.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;

public interface SubRoutineRepository extends JpaRepository<SubRoutine, HistoryPk> {
    Optional<SubRoutine> findBySubRoutinePk(HistoryPk historyPk);

    // routineId와 historyStartDateTime <= (현재시간) <= historyEndDateTime 조건을 모두 만족하는 루틴 조회
    Optional<SubRoutine> findBySubRoutinePk_IdAndHistoryStartDateTimeLessThanEqualAndHistoryEndDateTimeGreaterThanEqual(
        UUID routineId, LocalDateTime historyStartDateBound, LocalDateTime historyEndDateBound);

    List<SubRoutine> findByRoutineId(UUID routineId);
}
