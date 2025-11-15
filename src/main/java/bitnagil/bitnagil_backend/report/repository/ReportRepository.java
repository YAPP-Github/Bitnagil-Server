package bitnagil.bitnagil_backend.report.repository;

import bitnagil.bitnagil_backend.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
