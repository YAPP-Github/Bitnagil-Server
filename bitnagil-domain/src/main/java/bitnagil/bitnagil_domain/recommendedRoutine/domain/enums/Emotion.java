package bitnagil.bitnagil_domain.recommendedRoutine.domain.enums;

import bitnagil.common.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Emotion implements EnumType {
    VITALITY("활력"),
    FATIGUE("피로"),
    ANXIETY("불안"),
    CALM("평온"),
    SATISFACTION("만족"),
    LETHARGY("무기력");

    private final String description;
}
