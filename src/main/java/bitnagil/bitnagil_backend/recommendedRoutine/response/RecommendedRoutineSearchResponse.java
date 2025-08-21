package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineSearchResponse {

    // 추천 루틴 타입별 루틴, 서브루틴 리스트
    @Schema(description = "추천 루틴 타입을 key로 가지는 루틴 목록 Map입니다. Swagger에서는 additionalProp1처럼 보일 수 있습니다.")
    Map<String, List<RecommendedRoutineSearchResult>> recommendedRoutines;
    // 감정 구슬 enum 값
    @Schema(description = "감정 구슬 타입")
    EmotionMarbleType emotionMarbleType;
}
