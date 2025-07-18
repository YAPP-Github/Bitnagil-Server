package bitnagil.bitnagil_backend.routine.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class SubRoutineSearchResultDto {
    @Schema(example = "1")
    private UUID subRoutineId; // 서브 루틴 ID
    @Schema(example = "물 10초만에 마시기")
    private String subRoutineName; // 서브 루틴 이름
    @Schema(example = "false")
    private Boolean modifiedYn; // 수정 여부
    @Schema(example = "1")
    private Integer sortOrder; // 정렬 순서
    @Schema(example = "false", description = "true: 완료, false: 미완료 (default는 false)")
    private Boolean completeYn; // 완료 여부 (true: 완료, false: 미완료)
}
