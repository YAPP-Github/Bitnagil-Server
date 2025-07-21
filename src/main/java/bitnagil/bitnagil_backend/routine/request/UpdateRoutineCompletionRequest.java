package bitnagil.bitnagil_backend.routine.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "루틴 완료 여부 업데이트 DTO")
public class UpdateRoutineCompletionRequest {

    @Schema(description = "루틴 완료여부를 갱신하는 날짜입니다.",
            example = "2025-07-13",
            required = true)
    @NotNull
    private LocalDate performedDate;

    @Schema(
        description = "변경된 루틴 완료여부에 대한 정보를 담은 리스트입니다.",
        example = "[" +
            "{" +
            "\"routineType\": \"CHANGED_SUB_ROUTINE\", " +
            "\"routineId\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\", " +
            "\"historySeq\": 2, " +
            "\"isCompleted\": false" +
            "}, " +
            "{" +
            "\"routineType\": \"ROUTINE\", " +
            "\"routineId\": \"123e4567-e89b-12d3-a456-426614174000\", " +
            "\"historySeq\": 1, " +
            "\"isCompleted\": true" +
            "}" +
            "]",
        required = true
    )
    @NotNull
    private List<RoutineCompletionInfo> routineCompletionInfos;
}
