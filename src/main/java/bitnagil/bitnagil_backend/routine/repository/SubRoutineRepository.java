package bitnagil.bitnagil_backend.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.routine.domain.SubRoutine;

public interface SubRoutineRepository extends JpaRepository<SubRoutine, Long> {
}
