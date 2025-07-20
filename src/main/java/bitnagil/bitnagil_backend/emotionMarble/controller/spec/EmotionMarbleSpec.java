package bitnagil.bitnagil_backend.emotionMarble.controller.spec;

import bitnagil.bitnagil_backend.emotionMarble.request.RegisterEmotionMarbleRequest;
import bitnagil.bitnagil_backend.emotionMarble.response.EmotionMarbleTypeResponse;
import bitnagil.bitnagil_backend.emotionMarble.response.RegisterEmotionMarbleResponse;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.EMOTION_MARBLE)
public interface EmotionMarbleSpec {

    @Operation(summary = "감정 구슬을 조회합니다")
    public CustomResponseDto<EmotionMarbleTypeResponse> getEmotionMarbles();

    @Operation(summary = "감정 구슬을 등록합니다. 감정 구슬에 따른 추천 루틴을 응답합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE})
    public CustomResponseDto<RegisterEmotionMarbleResponse> registryEmotionMarble(User user, RegisterEmotionMarbleRequest request);
}
