package bitnagil.bitnagil_backend.emotionMarble.controller;

import bitnagil.bitnagil_backend.emotionMarble.controller.spec.EmotionMarbleSpec;
import bitnagil.bitnagil_backend.emotionMarble.request.RegisterEmotionMarbleRequest;
import bitnagil.bitnagil_backend.emotionMarble.response.EmotionMarbleTypeResponse;
import bitnagil.bitnagil_backend.emotionMarble.response.RegisterEmotionMarbleResponse;
import bitnagil.bitnagil_backend.emotionMarble.service.EmotionMarbleService;
import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/emotion-marbles")
public class EmotionMarbleController implements EmotionMarbleSpec {
    private final EmotionMarbleService emotionMarbleService;

    // 감정구슬 조회 API
    @GetMapping("")
    public CustomResponseDto<List<EmotionMarbleTypeResponse>> getEmotionMarbles() {
        return CustomResponseDto.from(emotionMarbleService.getEmotionMarbles());
    }

    // 감정구슬 등록 API
    @PostMapping("")
    public CustomResponseDto<RegisterEmotionMarbleResponse> registryEmotionMarble(@CurrentUser User user, @RequestBody RegisterEmotionMarbleRequest request) {
        return CustomResponseDto.from(emotionMarbleService.registryEmotionMarble(user, request));
    }
}
