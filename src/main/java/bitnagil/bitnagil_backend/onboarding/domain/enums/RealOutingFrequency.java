package bitnagil.bitnagil_backend.onboarding.domain.enums;

import bitnagil.bitnagil_backend.enums.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RealOutingFrequency implements EnumType {
    ZERO_PER_WEEK("일주일 0회"),
    ONE_TO_TWO_PER_WEEK("일주일 1~2회"),
    THREE_TO_FOUR_PER_WEEK("일주일 3~4회"),
    MORE_THAN_FIVE_PER_WEEK("일주일 5회 이상"),
    ;

    private final String description;
}
