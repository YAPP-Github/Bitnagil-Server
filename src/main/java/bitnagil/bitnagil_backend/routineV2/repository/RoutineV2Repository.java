package bitnagil.bitnagil_backend.routineV2.repository;

import java.time.LocalDate;
import java.util.List;

import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_backend.routineV2.domain.RoutineV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineV2Repository extends JpaRepository<RoutineV2, Long> {

    // 오늘 이후의 루틴 내역을 조회
    List<RoutineV2> findByRoutineInfoAndRoutineDateAfter(RoutineInfoV2 routineInfoV2, LocalDate date);
}
