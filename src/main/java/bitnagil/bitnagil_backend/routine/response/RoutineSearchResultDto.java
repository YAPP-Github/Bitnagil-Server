package bitnagil.bitnagil_backend.routine.response;

import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RoutineSearchResultDto {
    @Schema(example = "046259d9-352a-4fd3-9855-c4539fb19242")
    private UUID routineId; // 루틴 ID
    @Schema(example = "1")
    private Long historySeq; // 루틴 이력 시퀀스
    @Schema(example = "물마시기")
    private String routineName; // 루틴 이름
    @Schema(example = "[MONDAY, WEDNESDAY, FRIDAY]")
    private List<DayOfWeek> repeatDay; // 반복 요일
    @Schema(example = "08:30:00")
    private LocalTime executionTime; // 루틴 실행 시간
    private List<SubRoutineSearchResultDto> subRoutineSearchResultDto; // 서브루틴 목록
    @Schema(example = "false")
    private Boolean modifiedYn; // 수정 여부
    @Schema(example = "false", description = "true: 완료, false: 미완료 (default는 false)")
    private Boolean completeYn; // 완료 여부 (true: 완료, false: 미완료)
    @Schema(example = "ROUTINE", description = "루틴 구분을 위한 타입. 추후 루틴 완료 처리시 해당값을 그대로 전달")
    private RoutineType routineType; // 루틴 타입 (ROUTINE, SUB_ROUTINE, CHANGED_ROUTINE, CHANGED_SUB_ROUTINE)
}
