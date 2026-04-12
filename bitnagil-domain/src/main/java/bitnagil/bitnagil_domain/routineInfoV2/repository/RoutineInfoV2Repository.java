package bitnagil.bitnagil_domain.routineInfoV2.repository;

import bitnagil.bitnagil_domain.routineInfoV2.domain.RoutineInfoV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineInfoV2Repository extends JpaRepository<RoutineInfoV2, Long> {

}
