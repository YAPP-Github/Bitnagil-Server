package bitnagil.bitnagil_backend.emotionMarble.request;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 감정구슬의 종류를 조회하는 dto 입니다.
 */
@Getter
@NoArgsConstructor
@Schema(description = "감정구슬 등록 요청 DTO")
public class RegisterEmotionMarbleRequest {

    @Schema(description = "감정구술 enum 값",
            example = "CALM", required = true)
    @NotNull
    private EmotionMarbleType emotionMarbleType;
}
