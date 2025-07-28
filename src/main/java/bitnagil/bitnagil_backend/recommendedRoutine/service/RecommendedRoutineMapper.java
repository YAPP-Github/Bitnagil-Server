package bitnagil.bitnagil_backend.recommendedRoutine.service;

import java.util.List;

import org.springframework.stereotype.Component;

import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResult;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedSubRoutineSearchResult;

/**
 * 추천 루틴 관련해서 DB에서 조회해오거나 가공된 데이터를 DTO로 변환하는 Mapper 클래스입니다.
 */
@Component
public class RecommendedRoutineMapper {

    // 추천 루틴을 DTO로 변환
    public RecommendedRoutineDto toRecommendedRoutineDto(RecommendedRoutine recommendedRoutine,
        List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults) {

        return RecommendedRoutineDto.builder()
            .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
            .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
            .recommendedRoutineDescription(recommendedRoutine.getRecommendedRoutineDescription())
            .recommendedSubRoutineSearchResult(recommendedSubRoutineResults)
            .build();
    }

    // 추천 루틴을 RecommendedRoutineSearchResult으로 변환
    public RecommendedRoutineSearchResult toRecommendedRoutineSearchResult(
        RecommendedRoutine recommendedRoutine, List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults) {

        return RecommendedRoutineSearchResult.builder()
            .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
            .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
            .recommendedRoutineDescription(recommendedRoutine.getRecommendedRoutineDescription())
            .recommendedRoutineLevel(recommendedRoutine.getRecommendedRoutineLevel())
            .recommendedSubRoutineSearchResult(recommendedSubRoutineResults)
            .build();
    }

    // 추천 서브 루틴을 RecommendedSubRoutineSearchResult으로 변환
    public RecommendedSubRoutineSearchResult toRecommendedSubRoutineSearchResult(
        RecommendedSubRoutine recommendedSubRoutine) {

        return RecommendedSubRoutineSearchResult.builder()
            .recommendedSubRoutineId(recommendedSubRoutine.getRecommendedSubRoutineId())
            .recommendedSubRoutineName(recommendedSubRoutine.getSubRoutineName())
            .build();
    }
}
