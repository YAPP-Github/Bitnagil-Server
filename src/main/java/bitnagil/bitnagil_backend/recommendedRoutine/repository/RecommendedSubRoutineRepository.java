package bitnagil.bitnagil_backend.recommendedRoutine.repository;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedSubRoutineRepository extends JpaRepository<RecommendedSubRoutine, Long> {
    List<RecommendedSubRoutine> findByRecommendedRoutine(RecommendedRoutine recommendedRoutine);
}
