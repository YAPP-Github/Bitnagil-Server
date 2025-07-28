package bitnagil.bitnagil_backend.emotionMarble.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmotionMarbleType implements EnumType {
    CALM("평온함", 5L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_calm.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_calm.png"),
    VITALITY("활기참", 6L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_vitality.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_vitality.png"),
    LETHARGY("무기력함", 7L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_lethargy.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_lethargy.png"),
    ANXIETY("불안함", 8L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_anxiety.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_anxiety.png"),
    SATISFACTION("만족함", 9L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_satisfaction.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_satisfaction.png"),
    FATIGUE("피로함", 10L, "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/home_fatigue.png", "https://bitnagil-s3.s3.ap-northeast-2.amazonaws.com/marble_fatigue.png")
    ;

    private final String description;
    private final Long caseId;
    private final String homeMarbleImageUrl;
    private final String marbleImageUrl;
}
