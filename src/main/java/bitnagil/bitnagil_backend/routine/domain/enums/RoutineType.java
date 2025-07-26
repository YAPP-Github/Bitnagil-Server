package bitnagil.bitnagil_backend.routine.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoutineType implements EnumType {
    ROUTINE("루틴"),
    SUB_ROUTINE("서브루틴"),
    CHANGED_ROUTINE("변경 루틴"),
    CHANGED_SUB_ROUTINE("변경 서브루틴"),
    ;

    private final String description;
}
