package bitnagil.bitnagil_backend.emotionMarble.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 추천 루틴 상세에 대한 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
public class RecommendedSubRoutineDto {
    // 추천 루틴 상세 ID
    private Long recommendedSubRoutineId;
    // 추천 루틴 상세 이름
    private String recommendedSubRoutineName;
}
