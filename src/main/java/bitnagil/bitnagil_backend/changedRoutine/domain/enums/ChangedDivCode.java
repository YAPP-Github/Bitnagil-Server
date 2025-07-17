package bitnagil.bitnagil_backend.changedRoutine.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChangedDivCode implements EnumType {
    TIME_CHANGE("시간 변경"),
    DELAY("미루기"),
    TODAY_DELETE("오늘만 루틴 삭제"),
    ONBOARDING("온보딩 루틴")
    ;

    private final String description;
}
