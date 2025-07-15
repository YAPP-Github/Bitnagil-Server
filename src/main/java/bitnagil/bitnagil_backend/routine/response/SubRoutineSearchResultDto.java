package bitnagil.bitnagil_backend.routine.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SubRoutineSearchResultDto {
    @Schema(example = "1")
    private Long subRoutineId; // 서브 루틴 ID
    @Schema(example = "물 10초만에 마시기")
    private String subRoutineName; // 서브 루틴 이름
    // todo: 서브루틴 완료여부 추가
}
