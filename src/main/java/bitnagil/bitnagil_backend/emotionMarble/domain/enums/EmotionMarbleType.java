package bitnagil.bitnagil_backend.emotionMarble.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmotionMarbleType implements EnumType {
    CALM("평온함", 5L),
    VITALITY("활기참", 6L),
    LETHARGY("무기력함", 7L),
    ANXIETY("불안함", 8L),
    SATISFACTION("만족함", 9L),
    FATIGUE("피로함", 10L)
    ;

    private final String description;
    private final Long caseId;
}
