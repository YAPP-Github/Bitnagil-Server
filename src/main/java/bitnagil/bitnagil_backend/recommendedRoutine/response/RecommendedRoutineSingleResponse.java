package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

/**
 * 추천 루틴 단건 조회에 대한 응답 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineSingleResponse {
    @Schema(example = "1")
    private Long recommendedRoutineId; // 추천 루틴 ID
    @Schema(example = "물마시기")
    private String recommendedRoutineName; // 추천 루틴 이름
    @Schema(example = "08:00:00")
    private LocalTime recommendedRoutineExecutionTime; // 추천 루틴 실행 시간
    private List<RecommendedSubRoutineSingleResponse> recommendedSubRoutineSingleResponse; // 서브루틴 목록
}
