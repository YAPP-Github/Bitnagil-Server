package bitnagil.bitnagil_backend.recommendedRoutine.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 추천 루틴에 대한 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineDto {

    // 추천 루틴 ID
    private Long recommendedRoutineId;
    // 추천 루틴 이름
    private String recommendedRoutineName;
    // 추천 루틴 설명
    private String routineDescription;

    // 추천 루틴 상세 정보
    List<RecommendedSubRoutineDto> recommendedSubRoutines;
}
