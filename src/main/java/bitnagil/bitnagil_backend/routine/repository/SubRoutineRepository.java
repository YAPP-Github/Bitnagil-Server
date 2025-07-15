package bitnagil.bitnagil_backend.routine.repository;

import bitnagil.bitnagil_backend.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.SubRoutine;

import java.time.LocalDateTime;
import java.util.List;

public interface SubRoutineRepository extends JpaRepository<SubRoutine, Long> {
    /**
     * 현재 시점을 기준으로 살아있는 서브루틴 이력을 조회
     * historyStartDateTime < systime <= historyEndDateTime
     */
    List<SubRoutine> findByRoutineAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
            Routine routine,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
