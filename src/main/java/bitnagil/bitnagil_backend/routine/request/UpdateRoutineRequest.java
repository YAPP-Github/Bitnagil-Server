package bitnagil.bitnagil_backend.routine.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "루틴 수정 요청 DTO")
public class UpdateRoutineRequest{

    @Schema(description = "루틴 id(UUID) 값입니다.",
            example = "4fa85f64-5717-4562-b3fc-2c963f66afa6",
            required = true)
    @NotNull
    private UUID routineId;

    @Schema(description = "루틴 이름입니다.",
            example = "모닝 루틴",
            required = true)
    @NotNull
    private String routineName;

    @Schema(description = "반복 요일에 대한 리스트입니다.",
            example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]",
            required = true)
    @NotNull
    private List<DayOfWeek> repeatDay;

    @Schema(description = "루틴 시작 시간입니다.",
            example = "07:30:00",
            required = true)
    @NotNull
    private LocalTime executionTime;

    @Schema(
        description = "세부 루틴 수정에 대한 정보를 담은 리스트입니다.",
        example = "["
            + "{\"subRoutineId\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\", \"subRoutineName\": \"손 씻기\", \"sortOrder\": 1},"
            + "{\"subRoutineId\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\", \"subRoutineName\": null, \"sortOrder\": null},"
            + "{\"subRoutineId\": null, \"subRoutineName\": \"침대 정리하기\", \"sortOrder\": 2}"
            + "]",
        required = true
    )
    @NotNull
    private List<SubRoutineInfo> subRoutineInfos;

}