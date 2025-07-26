package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineSearchResult {
    // 추천 루틴 ID
    private Long recommendedRoutineId;
    // 추천 루틴 이름
    private String recommendedRoutineName;
    // 추천 루틴 설명
    private String recommendedRoutineDescription;
    // 추천 루틴 난이도
    private RecommendedRoutineLevel recommendedRoutineLevel;
    // 추천 서브 루틴 리스트
    private List<RecommendedSubRoutineSearchResult> recommendedSubRoutineDetailSearchResult;
}
