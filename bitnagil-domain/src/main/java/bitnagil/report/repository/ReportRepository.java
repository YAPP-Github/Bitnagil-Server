package bitnagil.report.repository;

import java.util.List;
import java.util.Optional;

import bitnagil.report.domain.Report;
import bitnagil.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserOrderByUpdatedAtDesc(User user);

    Optional<Report> findByReportIdAndUser(Long reportId, User user);
}
