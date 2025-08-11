package bitnagil.bitnagil_backend.routineV2.repository;

import bitnagil.bitnagil_backend.routineV2.domain.RoutineV2;
import bitnagil.bitnagil_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoutineV2Repository extends JpaRepository<RoutineV2, Long> {

    /**
     * startDate와 endDate 사이에 있는 루틴을 조회하는 메서드입니다.
     * fetch join을 사용하여 RoutineInfoV2와 함께 조회합니다.
     */
    @Query("""
        select r from RoutineV2 r
        join fetch r.routineInfo ri
        where ri.user = :user
          and r.routineDate between :startDate and :endDate
    """)
    List<RoutineV2> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
