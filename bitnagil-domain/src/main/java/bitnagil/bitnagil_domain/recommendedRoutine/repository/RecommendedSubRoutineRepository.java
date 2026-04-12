package bitnagil.bitnagil_domain.recommendedRoutine.repository;

import bitnagil.bitnagil_domain.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_domain.recommendedRoutine.domain.RecommendedSubRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedSubRoutineRepository extends JpaRepository<RecommendedSubRoutine, Long> {
    List<RecommendedSubRoutine> findByRecommendedRoutine(RecommendedRoutine recommendedRoutine);
}
