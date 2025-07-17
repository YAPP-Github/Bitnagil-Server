package bitnagil.bitnagil_backend.routine.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RoutineSearchResultDto {
    @Schema(example = "1")
    private UUID routineId; // 루틴 ID
    @Schema(example = "물마시기")
    private String routineName; // 루틴 이름
    // todo: 완료여부 추가
    @Schema(example = "08:30:00")
    private LocalTime executionTime; // 루틴 실행 시간
    private List<SubRoutineSearchResultDto> subRoutineSearchResultDto; // 서브루틴 목록
    @Schema(example = "false")
    private Boolean modifiedYn; // 수정 여부
}
