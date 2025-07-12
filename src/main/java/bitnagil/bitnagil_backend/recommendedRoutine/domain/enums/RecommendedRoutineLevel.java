package bitnagil.bitnagil_backend.recommendedRoutine.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RecommendedRoutineLevel {
    LEVEL1("레벨1"), // 레벨1
    LEVEL2("레벨2"), // 레벨2
    LEVEL3("레벨3"), // 레벨3
    LEVEL4("레벨4"), // 레벨4
    LEVEL5("레벨5"); // 레벨4

    private final String description;
}
