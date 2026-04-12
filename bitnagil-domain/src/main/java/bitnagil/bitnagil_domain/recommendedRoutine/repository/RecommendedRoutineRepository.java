package bitnagil.bitnagil_domain.recommendedRoutine.repository;

import bitnagil.bitnagil_domain.onboarding.domain.Case;
import bitnagil.bitnagil_domain.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.common.enums.RecommendedRoutineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedRoutineRepository extends JpaRepository<RecommendedRoutine, Long> {
    List<RecommendedRoutine> findByResultCase(Case resultCase);

    List<RecommendedRoutine> findByRecommendedRoutineType(RecommendedRoutineType value);
}
