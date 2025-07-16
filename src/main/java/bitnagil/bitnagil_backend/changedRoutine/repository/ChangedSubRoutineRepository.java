package bitnagil.bitnagil_backend.changedRoutine.repository;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

    public interface ChangedSubRoutineRepository extends JpaRepository<ChangedSubRoutine, HistoryPk> {

    /**
     * 현재 시점을 기준으로 살아있는 변경 서브루틴 이력을 조회
     * historyStartDateTime < systime <= historyEndDateTime
     */
    List<ChangedSubRoutine> findByChangedRoutineAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
            ChangedRoutine changedRoutine,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
