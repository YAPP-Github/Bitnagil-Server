package bitnagil.bitnagil_backend.recommendedRoutine.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecommendedSubRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendedSubRoutineId;

    private String routineDetailName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_routine_id")
    private RecommendedRoutine recommendedRoutine;

    public void setRecommendedRoutine(RecommendedRoutine recommendedRoutine) {
        this.recommendedRoutine = recommendedRoutine;
    }
}
