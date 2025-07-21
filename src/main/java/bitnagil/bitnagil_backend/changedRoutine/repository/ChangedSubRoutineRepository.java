package bitnagil.bitnagil_backend.changedRoutine.repository;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangedSubRoutineRepository extends JpaRepository<ChangedSubRoutine, HistoryPk> {

    Optional<ChangedSubRoutine> findByChangedSubRoutinePk(HistoryPk changedSubRoutinePk);

    /**
     * 현재 시점을 기준으로 살아있는 변경 서브루틴 이력을 조회
     * historyStartDateTime < systime <= historyEndDateTime
     */
    List<ChangedSubRoutine> findByChangedRoutineIdAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
            UUID changedRoutineId,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
