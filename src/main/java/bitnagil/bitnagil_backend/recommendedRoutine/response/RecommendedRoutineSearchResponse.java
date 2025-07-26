package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
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
    Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> recommendedRoutines;
    // 감정 구슬 enum 값
    EmotionMarbleType emotionMarbleType;
}
