package bitnagil.bitnagil_backend.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    boolean existsByName(String name);
}
