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
    @Schema(description = "감정 구슬 타입", example = "CALM")
    private EmotionMarbleType emotionMarbleType;

    @Schema(description = "감정 구슬 명칭", example = "평온함")
    private String emotionMarbleName;

    @Schema(description = "감정 구슬 이미지 URL", example = "https://example.com/image/calm.png")
    private String imageUrl;
}
