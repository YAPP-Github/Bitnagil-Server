package bitnagil.bitnagil_domain.changedRoutine.repository;

import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.common.entity.HistoryPk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChangedSubRoutineRepository extends JpaRepository<ChangedSubRoutine, HistoryPk> {

    Optional<ChangedSubRoutine> findByChangedSubRoutinePk(HistoryPk changedSubRoutinePk);

    List<ChangedSubRoutine> findByChangedRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
            UUID changedRoutineId,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
