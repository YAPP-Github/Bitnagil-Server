package bitnagil.bitnagil_backend.recommendedRoutine.repository;

import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedRoutineRepository extends JpaRepository<RecommendedRoutine, Long> {
    List<RecommendedRoutine> findByResultCase(Case resultCase);

    List<RecommendedRoutine> findByRecommendedRoutineType(RecommendedRoutineType value);
}
