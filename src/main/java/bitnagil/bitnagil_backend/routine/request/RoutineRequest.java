package bitnagil.bitnagil_backend.routine.request;

import java.time.LocalTime;
import java.util.List;

import bitnagil.bitnagil_backend.enums.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "루틴 등록 및 수정 요청 DTO")
public class RoutineRequest {

    @Schema(description = "루틴 이름입니다.",
            example = "아침 준비",
            required = true)
    @NotNull
    private String routineName;

    @Schema(description = "반복 요일에 대한 리스트입니다.",
            example = "[\"MONDAY\", \"FRIDAY\"]",
            required = true)
    @NotNull
    private List<DayOfWeek> daysOfWeek;

    @Schema(description = "루틴 시작 시간입니다.",
            example = "08:15:00")
    @NotNull
    private LocalTime executionTime;

    @Schema(description = "세부 루틴 이름에 대한 리스트입니다.",
            example = "[\"손 씻기\", \"세수 하기\", \"양치 하기\"]")
    private List<String> subRoutineName;
}
