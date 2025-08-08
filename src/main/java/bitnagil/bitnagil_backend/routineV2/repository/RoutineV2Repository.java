package bitnagil.bitnagil_backend.routineV2.repository;

import bitnagil.bitnagil_backend.routineV2.domain.RoutineV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineV2Repository extends JpaRepository<RoutineV2, Long> {

}
