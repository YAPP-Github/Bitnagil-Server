package bitnagil.bitnagil_backend.routine.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "루틴 수정 요청 DTO")
public class UpdateRoutineRequest implements RoutineRequestBase {

    @Schema(description = "루틴 ID 값입니다.",
            example = "1",
            required = true)
    @NotNull
    private Long routineId;

    @Schema(description = "루틴 이름입니다.",
            example = "모닝 루틴",
            required = true)
    private String routineName;

    @Schema(description = "반복 요일에 대한 리스트입니다.",
            example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]",
            required = true)
    private List<DayOfWeek> repeatDay;

    @Schema(description = "루틴 시작 시간입니다.",
            example = "07:30:00",
            required = true)
    private LocalTime executionTime;

    @Schema(description = "세부 루틴 이름에 대한 리스트입니다.",
            example = "[\"손 씻기\", \"침대 정리하기\", \"양치 하기\"]",
            required = true)
    private List<String> subRoutineName;
}
