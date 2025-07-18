package bitnagil.bitnagil_backend.routine.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "회원이 보유한 특정 기간의 루틴을 조회하는 요청 DTO")
public class RoutineSearchRequest {

    @Schema(description = "조회 시작 날짜", example = "2025-07-01")
    @NotNull
    private LocalDate startDate;

    @Schema(description = "조회 종료 날짜", example = "2025-07-13")
    @NotNull
    private LocalDate endDate;
}
