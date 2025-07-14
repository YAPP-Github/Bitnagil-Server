package bitnagil.bitnagil_backend.routine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;

public interface SubRoutineRepository extends JpaRepository<SubRoutine, Long> {
    List<SubRoutine> findByRoutine(Routine routine);
}
