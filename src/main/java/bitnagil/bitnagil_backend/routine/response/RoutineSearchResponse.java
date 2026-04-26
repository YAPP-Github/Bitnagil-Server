package bitnagil.bitnagil_backend.routine.response;

import bitnagil.emotionMarble.domain.enums.EmotionMarbleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class RoutineSearchResponse {
    @Schema(description = "날짜(LocalDate: 2025-07-01)와 같은 형태를 key로 가지는 루틴 목록 Map입니다. Swagger에서는 additionalProp1처럼 보일 수 있습니다.")
    private Map<LocalDate, List<RoutineSearchResultDto>> routines; // 날짜별 루틴 목록
}
