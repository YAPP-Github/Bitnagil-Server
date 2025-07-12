package bitnagil.bitnagil_backend.changedRoutine.repository;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangedRoutineRepository extends JpaRepository<ChangedRoutine, Long> {
}
