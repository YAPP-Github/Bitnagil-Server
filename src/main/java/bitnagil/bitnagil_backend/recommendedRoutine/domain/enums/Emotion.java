package bitnagil.bitnagil_backend.recommendedRoutine.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Emotion implements EnumType {
    VITALITY("활력"),
    STABILITY("안정"),
    DEPRESSION("우울"),
    LETHARGY("무기력"),
    JOY("기쁨");

    private final String description;
}
