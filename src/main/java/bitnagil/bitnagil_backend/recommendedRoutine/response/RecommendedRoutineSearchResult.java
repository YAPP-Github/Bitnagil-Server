package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineLevel;
import bitnagil.common.enums.RecommendedRoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineSearchResult {
    // 추천 루틴 ID
    @Schema(description = "추천 루틴 ID", example = "1")
    private Long recommendedRoutineId;
    // 추천 루틴 이름
    @Schema(description = "추천 루틴 이름", example = "물마시기")
    private String recommendedRoutineName;
    // 추천 루틴 설명
    @Schema(description = "추천 루틴 설명", example = "하루에 물을 많이 마셔보아요")
    private String recommendedRoutineDescription;
    // 추천 루틴 난이도
    @Schema(description = "추천 루틴 난이도", example = "LEVEL1")
    private RecommendedRoutineLevel recommendedRoutineLevel;
    // 추천 루틴 수행 시간
    @Schema(description = "추천 루틴 수행 시간", example = "08:00:00")
    private LocalTime executionTime; // HH:mm 형식으로 변환된 수행 시간
    @Schema(description = "추천 루틴 타입", example = "WAKE_UP")
    private RecommendedRoutineType recommendedRoutineType;
    // 추천 서브 루틴 리스트
    private List<RecommendedSubRoutineSearchResult> recommendedSubRoutineSearchResult;
}
