package bitnagil.bitnagil_backend.recommendedRoutine.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.repository.EmotionMarbleRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.enums.RecommendedRoutineType;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResponse;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineSearchResult;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedSubRoutineSearchResult;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendedRoutineService {

    private final RecommendedRoutineRepository recommendedRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;
    private final EmotionMarbleRepository emotionMarbleRepository;
    private final UserRepository userRepository;

    private final RecommendedRoutineMapper recommendedRoutineMapper;

    /**
     * 추천 카테고리별 루틴, 서브루틴을 조회
     * key: 추천 루틴 타입 value: 추천 루틴, 서브 루틴 리스트
     */
    @Transactional(readOnly = true)
    public RecommendedRoutineSearchResponse searchRecommendedRoutines(User user) {

        LocalDate nowDate = LocalDate.now();

        // 카테고리 별 추천루틴에 대한 response 객체 생성
        Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response = new HashMap<>();
        response.put(RecommendedRoutineType.PERSONALIZED, new ArrayList<>()); // 맞춤 루틴은 미리 초기화 한다.(감정구슬, 온보딩 결과를 넣기 위해)

        // 영속성 객체에 user를 저장하기 위해 user를 조회
        
        user = userRepository.findByUserPk(user.getUserPk())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 맞춤 추천(감정구슬 + 온보딩)을 조회하고 response에 추가
        EmotionMarble emotionMarble = addPersonalizedRecommendedRoutine(user, nowDate, response);

        // 맞춤추천 이외의 카테고리에 대한 추천 루틴을 response 추가
        addCategoryRecommendedRoutines(response);

        return RecommendedRoutineSearchResponse.builder()
                .recommendedRoutines(response)
                .emotionMarbleType(emotionMarble == null ? null : emotionMarble.getEmotionMarbleType()) // 감정 구슬 타입 설정
                .build();
    }

    // 감정 구슬에 따른 추천 루틴 응답
    public List<RecommendedRoutineDto> recommendRoutinesByEmotionMarble(Case routineCase) {
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(routineCase);
        if (recommendedRoutines.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE);
        }

        // 응답 생성
        return recommendedRoutines.stream()
            .map(this::toRecommendedRoutineDtoWithDetails)
            .collect(Collectors.toList());
    }

    private RecommendedRoutineDto toRecommendedRoutineDtoWithDetails(RecommendedRoutine recommendedRoutine) {

        List<RecommendedSubRoutineSearchResult> recommendedRoutineDetailDtoList = recommendedSubRoutineRepository.findByRecommendedRoutine(
                recommendedRoutine)
            .stream()
            .map(recommendedRoutineMapper::toRecommendedSubRoutineSearchResult)
            .toList();

        // 추천 루틴을 dto로 변환한다.
        return recommendedRoutineMapper.toRecommendedRoutineDto(recommendedRoutine, recommendedRoutineDetailDtoList);
    }

    private void addCategoryRecommendedRoutines(Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
        RecommendedRoutineType[] values = RecommendedRoutineType.values();

        for (RecommendedRoutineType value : values) {
            // value가 PERSONALIZED가 아닌 경우에만 추천 루틴 조회
            if (value == RecommendedRoutineType.PERSONALIZED) {
                continue;
            }
            // 추천 루틴 조회(상위 4개만 조회)
            List<RecommendedRoutine> recommendedRoutines =
                recommendedRoutineRepository.findTop4ByRecommendedRoutineTypeOrderByRecommendedRoutineIdAsc(value);
            List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
                recommendedRoutines);
            // Map에 값을 저장
            response.put(value, recommendedRoutineResults);
        }
    }

    private  EmotionMarble addPersonalizedRecommendedRoutine(User user, LocalDate nowDate,
        Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
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
        return emotionMarble;
    }

    private List<RecommendedRoutineSearchResult> buildRecommendedRoutineSearchResult(
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
    private void addRecommendedRoutineToResponse(RecommendedRoutine recommendedRoutine,
                                                 List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults,
                                                 List<RecommendedRoutineSearchResult> recommendedRoutineResults) {

        RecommendedRoutineSearchResult recommendedRoutineResult =
            recommendedRoutineMapper.toRecommendedRoutineSearchResult(recommendedRoutine, recommendedSubRoutineResults);
        recommendedRoutineResults.add(recommendedRoutineResult);
    }

    // 추천 서브루틴을 응답 객체에 추가하는 메서드
    private void addRecommendedSubRoutineToResponse(List<RecommendedSubRoutine> recommendedSubRoutines,
                                                    List<RecommendedSubRoutineSearchResult> recommendedSubRoutineResults) {

        for (RecommendedSubRoutine recommendedSubRoutine : recommendedSubRoutines) {
            RecommendedSubRoutineSearchResult recommendedSubRoutineResult =
                recommendedRoutineMapper.toRecommendedSubRoutineSearchResult(recommendedSubRoutine);
            recommendedSubRoutineResults.add(recommendedSubRoutineResult);
        }
    }

    // 감정구슬에 따른 추천 루틴을 생성하는 메서드
    private void makeEmotionMarbleResponse(EmotionMarble emotionMarble,
                                           Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = emotionMarble.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
            recommendedRoutines);
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED).addAll(recommendedRoutineResults);
    }

    // 온보딩에 따른 추천 루틴을 생성하는 메서드
    private void makeOnboardingResponse(Onboarding onboarding,
                                        Map<RecommendedRoutineType, List<RecommendedRoutineSearchResult>> response) {
        Case resultCase = onboarding.getResultCase();
        List<RecommendedRoutine> recommendedRoutines = recommendedRoutineRepository.findByResultCase(resultCase);
        List<RecommendedRoutineSearchResult> recommendedRoutineResults = buildRecommendedRoutineSearchResult(
            recommendedRoutines);
        // 감정구슬에 따른 추천 루틴을 Map에 저장
        response.get(RecommendedRoutineType.PERSONALIZED).addAll(recommendedRoutineResults);
    }
}
