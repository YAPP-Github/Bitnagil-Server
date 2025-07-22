package bitnagil.bitnagil_backend.routine.request;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "선택한 요일(당일)만 루틴 삭제 DTO")
public class DeleteRoutineByDayRequest {

    @Schema(description = "삭제할 루틴 수행 날짜입니다.",
            example = "2025-07-13",
            required = true)
    @NotNull
    private LocalDate performedDate;

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
}
