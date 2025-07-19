package bitnagil.bitnagil_backend.emotionMarble.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 구슬 선택에 대한 추천 루틴 응답 DTO 클래스
 */
@Getter
@AllArgsConstructor
@Builder
@Schema(description = "감정 구슬 등록 응답 DTO")
public class RegisterEmotionMarbleResponse {

    private List<RecommendedRoutineDto> recommendedRoutines;

}
