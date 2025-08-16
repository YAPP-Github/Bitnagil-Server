package bitnagil.bitnagil_backend.recommendedRoutine.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResult;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedSubRoutineSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 추천 루틴 관련 객체 생성, 초기화 책임을 담당하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class RecommendedRoutineFactory {

    private final RecommendedRoutineRepository recommendedRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;
    private final RecommendedRoutineMapper recommendedRoutineMapper;

    /**
     * 맞춤추천 루틴을 제외한 카테고리별 추천 루틴, 서브루틴 응답을 생성하는 메서드
     * 캐싱을 위해 서비스 클래스가 아닌 Factory 클래스로 분리
     */
    @Cacheable(cacheNames = "categoryRecommendedRoutine", key = "'categoryRecommendedRoutine'")
    public Map<String, List<RecommendedRoutineSearchResult>> addCategoryRecommendedRoutines() {
        Map<String, List<RecommendedRoutineSearchResult>> response = new HashMap<>();
        RecommendedRoutineType[] values = RecommendedRoutineType.values();

        for (RecommendedRoutineType value : values) {
            // value가 PERSONALIZED가 아닌 경우에만 추천 루틴 조회
            if (value == RecommendedRoutineType.PERSONALIZED) {
                continue;
            }
            // 추천 루틴 조회
            List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByRecommendedRoutineType(value);
            List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
                    recommendedRoutines);
            // Map에 값을 저장
            response.put(value.name(), recommendedRoutineResults);
        }

        return response;
    }

    // 추천 서브루틴 응답 객체를 생성하여 리스트에 추가
    public List<RecommendedRoutineSearchResult> buildRecommendedRoutineSearchResult(
            List<RecommendedRoutine> recommendedRoutines) {
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = new ArrayList<>(); // 추천 루틴 응답 객체
        // 추천 서브루틴 조회
        for (RecommendedRoutine recommendedRoutine : recommendedRoutines) {
            List<RecommendedSubRoutine> recommendedSubRoutines = recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);
            List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults = new ArrayList<>();
            // 추천 서브루틴 응답 객체 생성
            addRecommendedSubRoutineToResponse(recommendedSubRoutines, recommendedSubRoutineResults);
            // 추천 루틴 응답 객체 생성
            addRecommendedRoutineToResponse(recommendedRoutine, recommendedSubRoutineResults, recommendedRoutineResults);
        }
        return recommendedRoutineResults;
    }

    // 추천루틴을 응답 객체에 추가하는 메서드
    public void addRecommendedRoutineToResponse(RecommendedRoutine recommendedRoutine,
                                                 List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults,
                                                 List<RecommendedRoutineSearchResult> recommendedRoutineResults) {

        RecommendedRoutineSearchResult recommendedRoutineResult =
                recommendedRoutineMapper.toRecommendedRoutineSearchResult(recommendedRoutine, recommendedSubRoutineResults);
        recommendedRoutineResults.add(recommendedRoutineResult);
    }

    // 추천 서브루틴을 응답 객체에 추가하는 메서드
    public void addRecommendedSubRoutineToResponse(List<RecommendedSubRoutine> recommendedSubRoutines,
                                                    List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults) {

        for (RecommendedSubRoutine recommendedSubRoutine : recommendedSubRoutines) {
            RecommendedSubRoutineSearchResult recommendedSubRoutineResult =
                    recommendedRoutineMapper.toRecommendedSubRoutineSearchResult(recommendedSubRoutine);
            recommendedSubRoutineResults.add(recommendedSubRoutineResult);
        }
    }

    // 감정구슬에 따른 추천 루틴을 생성하는 메서드
    public void makeEmotionMarbleResponse(EmotionMarble emotionMarble,
                                           Map<String, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = emotionMarble.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
                recommendedRoutines);
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED.name()).addAll(recommendedRoutineResults);
    }

    // 온보딩에 따른 추천 루틴을 생성하는 메서드
    public void makeOnboardingResponse(Onboarding onboarding,
                                        Map<String, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = onboarding.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
                recommendedRoutines);
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED.name()).addAll(recommendedRoutineResults);
    }
}
