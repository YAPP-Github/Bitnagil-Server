package bitnagil.bitnagil_backend.routine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.routine.domain.RoutineCompletion;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;

public interface RoutineCompletionRepository extends JpaRepository<RoutineCompletion, HistoryPk> {

    Optional<RoutineCompletion> findByRoutineIdAndRoutineHistorySeqAndRoutineType(
        UUID routineId, Long routineHistorySeq, RoutineType routineType);
}
