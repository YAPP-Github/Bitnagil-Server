package bitnagil.bitnagil_backend.kpi.repository;

import bitnagil.bitnagil_backend.kpi.domain.MonthlyKpi;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyKpiRepository extends JpaRepository<MonthlyKpi, Long> {

    Optional<MonthlyKpi> findByTargetMonth(LocalDate targetMonth);

    boolean existsByTargetMonth(LocalDate targetMonth);
}
