package bitnagil.bitnagil_backend.routine.request;

import java.util.UUID;

import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineCompletionInfo {

    @Schema(description = "갱신할 루틴의 타입입니다.",
        example = "CHANGED_SUB_ROUTINE",
        required = true)
    @NotNull
    private RoutineType routineType;

    @Schema(description = "루틴의 ID 값입니다.",
        example = "4fa85f64-5717-4562-b3fc-2c963f66afa6",
        required = true)
    @NotNull
    private UUID routineId;

    @Schema(description = "루틴의 이력순번 값입니다.",
        example = "2",
        required = true)
    @NotNull
    private Long historySeq;

    @Schema(description = "갱신할 루틴의 완료 여부입니다.",
        example = "false",
        required = true)
    @NotNull
    private Boolean isCompleted;
}
