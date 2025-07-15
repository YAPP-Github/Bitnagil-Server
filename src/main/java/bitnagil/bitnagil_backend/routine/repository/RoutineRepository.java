package bitnagil.bitnagil_backend.routine.repository;

import bitnagil.bitnagil_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.Routine;

import java.time.LocalDateTime;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    boolean existsByName(String name);

    /**
     * 현재 시점을 기준으로 유저의 살아있는 루틴 이력을 조회
     * historyStartDate < systime <= historyEndDate
     */
    List<Routine> findByUserAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
            User user,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
