package bitnagil.bitnagil_backend.routine.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "선택한 요일(당일)만 루틴 삭제 DTO")
public class DeleteRoutineByDayRequest {

    @Schema(description = "루틴완료 여부 ID입니다. 해당 값이 없는 경우에는 null로 보내주세요.",
            example = "1")
    private Long routineCompletionId;

    @Schema(description = "루틴의 ID 값입니다.",
            example = "4fa85f64-5717-4562-b3fc-2c963f66afa6",
            required = true)
    @NotNull
    private UUID routineId;

    @Schema(description = "루틴에 대한 타입 값입니다.",
            example = "CHANGED_ROUTINE")
    private RoutineType routineType;

    @Schema(description = "세부루틴 완료 여부 정보를 담은 리스트입니다.",
            example = "["
                + "{\"routineCompletionId\": 3, \"subRoutineId\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\"},"
                + "{\"routineCompletionId\": null, \"subRoutineId\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\"},"
                + "{\"routineCompletionId\": 8, \"subRoutineId\": \"3e1e63bb-1e24-4e88-93e2-8ccd82215e08\"}"
                + "]")
    private List<SubRoutineInfoForDelete> subRoutineInfosForDelete;

    @Schema(description = "삭제할 루틴 수행 날짜입니다.",
            example = "2025-07-13",
            required = true)
    @NotNull
    private LocalDate performedDate;

    @Schema(description = "루틴의 이력순번 값입니다.",
            example = "2",
            required = true)
    @NotNull
    private Long historySeq;
}
