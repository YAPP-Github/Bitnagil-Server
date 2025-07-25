package bitnagil.bitnagil_backend.recommendedRoutine.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecommendedSubRoutineSearchResult {
    private Long recommendedSubRoutineId; // 추천 서브 루틴 ID
    private String recommendedSubRoutineName; // 추천 서브 루틴 이름
}
