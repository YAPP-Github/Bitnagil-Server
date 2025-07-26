package bitnagil.bitnagil_backend.emotionMarble.response;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "감정 구슬 조회 DTO")
public class EmotionMarbleTypeResponse {
    @Schema(
            description = "감정 구슬 enum 배열",
            type = "array",
            example = "[\"CALM\", \"VITALITY\", \"LETHARGY\", \"ANXIETY\", \"SATISFACTION\", \"FATIGUE\"]"
    )
    private EmotionMarbleType[] emotionMarbleTypes;
}
