package bitnagil.bitnagil_backend.routine.response;

import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class SubRoutineSearchResultDto {
    @Schema(example = "046259d9-352a-4fd3-9855-c4539fb19242")
    private UUID subRoutineId; // 서브 루틴 ID
    @Schema(example = "1")
    private Long historySeq;
    @Schema(example = "물 10초만에 마시기")
    private String subRoutineName; // 서브 루틴 이름
    @Schema(example = "false")
    private Boolean modifiedYn; // 수정 여부
    @Schema(example = "1")
    private Integer sortOrder; // 정렬 순서
    @Schema(example = "false", description = "true: 완료, false: 미완료 (default는 false)")
    private Boolean completeYn; // 완료 여부 (true: 완료, false: 미완료)
    @Schema(example = "SUB_ROUTINE", description = "루틴 구분을 위한 타입. 추후 루틴 완료 처리시 해당값을 그대로 전달")
    private RoutineType routineType; // 루틴 타입 (ROUTINE, SUB_ROUTINE, CHANGED_ROUTINE, CHANGED_SUB_ROUTINE)
}
