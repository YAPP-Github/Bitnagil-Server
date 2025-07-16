package bitnagil.bitnagil_backend.changedRoutine.repository;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;

import bitnagil.bitnagil_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ChangedRoutineRepository extends JpaRepository<ChangedRoutine, HistoryPk> {

    /**
     * 현재 시점을 기준으로 유저의 살아있는 변경루틴 이력을 조회
     * historyStartDateTime < systime <= historyEndDateTime
     */
    List<ChangedRoutine> findByUserAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqualAndChangedRoutineDateBetween(
            User user,
            LocalDateTime now1,
            LocalDateTime now2,
            LocalDate startDate,
            LocalDate endDate
    );
}
