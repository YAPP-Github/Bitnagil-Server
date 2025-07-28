package bitnagil.bitnagil_backend.recommendedRoutine.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "추천 서브 루틴 ID", example = "1")
    private Long recommendedSubRoutineId; // 추천 서브 루틴 ID
    @Schema(description = "추천 서브 루틴 이름", example = "물 2L 마시기")
    private String recommendedSubRoutineName; // 추천 서브 루틴 이름
}
