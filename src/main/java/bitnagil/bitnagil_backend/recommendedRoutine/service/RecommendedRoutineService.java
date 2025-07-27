package bitnagil.bitnagil_backend.recommendedRoutine.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.repository.EmotionMarbleRepository;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.response.*;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendedRoutineService {

    private final RecommendedRoutineRepository recommendedRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;
    private final EmotionMarbleRepository emotionMarbleRepository;
    private final UserRepository userRepository;

    /**
     * 추천 카테고리별 루틴, 서브루틴을 조회
     * key: 추천 루틴 타입 value: 추천 루틴, 서브 루틴 리스트
     */
    @Transactional(readOnly = true)
    public RecommendedRoutineSearchResponse searchRecommendedRoutines(User user) {

        LocalDate nowDate = LocalDate.now();
        LocalDateTime startDateTime = nowDate.atStartOfDay();
        LocalDateTime endDateTime = nowDate.atTime(LocalTime.MAX);

        // response 객체 생성
        Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response = new HashMap<>();
        response.put(RecommendedRoutineType.PERSONALIZED, new ArrayList<>()); // 맞춤 루틴은 미리 초기화 한다.(감정구슬, 온보딩 결과를 넣기 위해)

        // 영속성 객체에 user를 저장하기 위해 user를 조회
        user = userRepository.findByUserPk(user.getUserPk())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        /**
         * 맞춤 추천(감정구슬 + 온보딩)을 조회한다.
         */
        // 감정구슬(당일에 감정구슬을 선택한 경우만 조회)
        EmotionMarble emotionMarble = emotionMarbleRepository.findByUserIdAndDateIs(user.getUserPk().getId(), nowDate);
        if(emotionMarble != null) { // 조회 결과가 존재하는 경우
            makeEmotionMarbleResponse(emotionMarble, response);
        }

        // 온보딩 결과에 따른 추천 루틴 조회
        Onboarding onboarding = user.getOnboarding();
        if (onboarding != null) { // 온보딩을 수행한 유저의 경우(온보딩은 필수지만 방어 로직으로 추가)
            makeOnboardingResponse(onboarding, response);
        }

        /**
         * 맞춤추천이 아닌 나머지 추천 루틴 카테고리에 대해
         */
        RecommendedRoutineType[] values = RecommendedRoutineType.values();
        for (RecommendedRoutineType value : values) {
            // value가 PERSONALIZED가 아닌 경우에만 추천 루틴 조회
            if (value == RecommendedRoutineType.PERSONALIZED) {
                continue;
            }
            // 추천 루틴 조회(상위 4개만 조회)
            List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findTop4ByRecommendedRoutineTypeOrderByRecommendedRoutineIdAsc(value);
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
            // Map에 값을 저장
            response.put(value, recommendedRoutineResults);
        }
        return RecommendedRoutineSearchResponse.builder()
                .recommendedRoutines(response)
                .emotionMarbleType(emotionMarble == null ? null : emotionMarble.getEmotionMarbleType()) // 감정 구슬 타입 설정
                .build();
    }

    /**
     * 추천 루틴 단건 조회
     */
    @Transactional(readOnly = true)
    public RecommendedRoutineSingleResponse searchRecommendedRoutine(Long recommendedRoutineId) {
        RecommendedRoutine recommendedRoutine = recommendedRoutineRepository.findById(recommendedRoutineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE));

        List<RecommendedSubRoutineSingleResponse> recommendedSubRoutineResponse = new ArrayList<>();
        // 추천 루틴에 해당하는 서브루틴 조회
        List<RecommendedSubRoutine> recommendedSubRoutines = recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);
        for (RecommendedSubRoutine recommendedSubRoutine : recommendedSubRoutines) {
            RecommendedSubRoutineSingleResponse recommendedSubRoutineSingleResponse = RecommendedSubRoutineSingleResponse.builder()
                    .recommendedSubRoutineId(recommendedSubRoutine.getRecommendedSubRoutineId())
                    .recommendedSubRoutineName(recommendedSubRoutine.getSubRoutineName())
                    .build();
            recommendedSubRoutineResponse.add(recommendedSubRoutineSingleResponse);
        }

        return RecommendedRoutineSingleResponse.builder()
                .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
                .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
                .recommendedRoutineExecutionTime(recommendedRoutine.getExecutionTime())
                .recommendedSubRoutineSingleResponse(recommendedSubRoutineResponse)
                .build();
    }

    // 추천루틴을 응답 객체에 추가하는 메서드
    private void addRecommendedRoutineToResponse(RecommendedRoutine recommendedRoutine,
                                                 List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults,
                                                 List<RecommendedRoutineSearchResult> recommendedRoutineResults) {
        RecommendedRoutineSearchResult recommendedRoutineResult  = RecommendedRoutineSearchResult.builder()
                .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
                .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
                .recommendedRoutineDescription(recommendedRoutine.getRecommendedRoutineDescription())
                .recommendedRoutineLevel(recommendedRoutine.getRecommendedRoutineLevel())
                .recommendedSubRoutineDetailSearchResult(recommendedSubRoutineResults)
                .build();
        recommendedRoutineResults.add(recommendedRoutineResult);
    }

    // 추천 서브루틴을 응답 객체에 추가하는 메서드
    private void addRecommendedSubRoutineToResponse(List<RecommendedSubRoutine> recommendedSubRoutines,
                                                    List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults) {
        for (RecommendedSubRoutine recommendedSubRoutine : recommendedSubRoutines) {
            RecommendedSubRoutineSearchResult recommendedSubRoutineResult = RecommendedSubRoutineSearchResult.builder()
                    .recommendedSubRoutineId(recommendedSubRoutine.getRecommendedSubRoutineId())
                    .recommendedSubRoutineName(recommendedSubRoutine.getSubRoutineName())
                    .build();
            recommendedSubRoutineResults.add(recommendedSubRoutineResult);
        }
    }

    // 감정구슬에 따른 추천 루틴을 생성하는 메서드
    private void makeEmotionMarbleResponse(EmotionMarble emotionMarble,
                                           Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = emotionMarble.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = new ArrayList<>();
        for (RecommendedRoutine recommendedRoutine : recommendedRoutines) {
            List<RecommendedSubRoutine> recommendedSubRoutines = recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);
            List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults = new ArrayList<>(); // 서브루틴 응답 객체
            // 추천 서브루틴 응답 객체 생성
            addRecommendedSubRoutineToResponse(recommendedSubRoutines, recommendedSubRoutineResults);
            // 추천 루틴 응답 객체 생성
            addRecommendedRoutineToResponse(recommendedRoutine, recommendedSubRoutineResults, recommendedRoutineResults);
        }
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED).addAll(recommendedRoutineResults);
    }

    // 온보딩에 따른 추천 루틴을 생성하는 메서드
    private void makeOnboardingResponse(Onboarding onboarding,
                                        Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = onboarding.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = new ArrayList<>();
        for (RecommendedRoutine recommendedRoutine : recommendedRoutines) {
            List<RecommendedSubRoutine> recommendedSubRoutines = recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);
            List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults = new ArrayList<>(); // 서브루틴 응답 객체
            // 추천 서브루틴 응답 객체 생성
            addRecommendedSubRoutineToResponse(recommendedSubRoutines, recommendedSubRoutineResults);
            // 추천 루틴 응답 객체 생성
            addRecommendedRoutineToResponse(recommendedRoutine, recommendedSubRoutineResults, recommendedRoutineResults);
        }
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED).addAll(recommendedRoutineResults);
    }
}
