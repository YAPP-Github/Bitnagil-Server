package bitnagil.bitnagil_backend.recommendedRoutine.response;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class RecommendedRoutineSearchResponse {
    Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> recommendedRoutines;
}
