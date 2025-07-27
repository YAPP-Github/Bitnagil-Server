package bitnagil.bitnagil_backend.recommendedRoutine.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 추천 루틴 상세에 대한 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
public class RecommendedSubRoutineSearchResult {
    private Long recommendedSubRoutineId; // 추천 서브 루틴 ID
    private String recommendedSubRoutineName; // 추천 서브 루틴 이름
}
