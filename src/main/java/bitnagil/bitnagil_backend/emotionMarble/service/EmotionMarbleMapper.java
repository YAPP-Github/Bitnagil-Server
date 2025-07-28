package bitnagil.bitnagil_backend.emotionMarble.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.emotionMarble.response.EmotionMarbleTypeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 감정구슬에 대한 가공된 데이터를 DTO로 변환하는 Mapper 클래스입니다.
 */
@Component
public class EmotionMarbleMapper {

    public EmotionMarbleTypeResponse toEmotionMarbleTypeResponse(EmotionMarbleType emotionMarbleType) {
        return EmotionMarbleTypeResponse.builder()
                .emotionMarbleName(emotionMarbleType.getDescription())
                .emotionMarbleType(emotionMarbleType)
                .imageUrl(emotionMarbleType.getMarbleImageUrl())
                .build();
    }
}
