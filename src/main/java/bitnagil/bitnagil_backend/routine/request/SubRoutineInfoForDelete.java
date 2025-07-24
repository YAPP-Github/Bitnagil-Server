package bitnagil.bitnagil_backend.routine.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 선택한 요일(당일)만 삭제 API 요청 시 필요한 서브 루틴 완료에 대한 정보를 관리하는 클래스입니다.
 */
@Getter
@NoArgsConstructor
public class SubRoutineInfoForDelete {

    @Schema(description = "세부루틴 완료 여부 ID입니다. 해당 값이 없는 경우에는 null로 보내주세요.")
    private Long routineCompletionId;

    @Schema(description = "서브 루틴의 ID 값입니다.",
        example = "4fa85f64-5717-4562-b3fc-2c963f66afa6",
        required = true)
    @NotNull
    private UUID subRoutineId;
}
