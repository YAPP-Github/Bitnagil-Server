package bitnagil.bitnagil_backend.routine.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubRoutineInfo {
    @Schema(description = "서브루틴 식별자",
            example = "4fa85f64-5717-4562-b3fc-2c963f66afa6",
            required = true)
    @NotNull
    private UUID subRoutineId;

    @Schema(description = "서브루틴 이름",
            example = "손 씻기",
            required = true)
    private String subRoutineName;

    @Schema(description = "서브루틴 정렬 순서",
            example = "1",
            required = true)
    private Integer sortOrder;
}
