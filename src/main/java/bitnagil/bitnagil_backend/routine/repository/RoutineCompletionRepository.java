package bitnagil.bitnagil_backend.routine.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.RoutineCompletion;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;

public interface RoutineCompletionRepository extends JpaRepository<RoutineCompletion, Long> {

    RoutineCompletion findByRoutineIdAndRoutineHistorySeqAndRoutineType(
        UUID routineId, Long routineHistorySeq, RoutineType routineType);

    Optional<RoutineCompletion> findByPerformedDateAndRoutineIdAndRoutineHistorySeqAndRoutineType(
        LocalDate performedDate, UUID routineId, Long routineHistorySeq, RoutineType routineType);
}
