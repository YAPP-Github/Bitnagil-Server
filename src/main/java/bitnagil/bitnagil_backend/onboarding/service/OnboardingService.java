package bitnagil.bitnagil_backend.onboarding.service;

import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_domain.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_domain.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_domain.changedRoutine.service.ChangedRoutineFactory;
import bitnagil.common.errorcode.ErrorCode;
import bitnagil.common.exception.CustomException;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_domain.onboarding.domain.Onboarding;
import bitnagil.bitnagil_domain.onboarding.domain.enums.EmotionType;
import bitnagil.bitnagil_domain.onboarding.repository.OnboardingRepository;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequestV2;
import bitnagil.bitnagil_backend.onboarding.request.RegistrationRoutinesRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_domain.recommendedRoutine.dto.response.RecommendedRoutineDto;
import bitnagil.bitnagil_domain.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_domain.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_domain.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_domain.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.service.RecommendedRoutineManager;
import bitnagil.bitnagil_domain.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_domain.routineInfoV2.repository.RoutineInfoV2Repository;
import bitnagil.bitnagil_backend.routineInfoV2.service.RoutineInfoV2Factory;
import bitnagil.bitnagil_domain.rountineV2.domain.RoutineV2;
import bitnagil.bitnagil_domain.rountineV2.repository.RoutineV2Repository;
import bitnagil.bitnagil_backend.routineV2.service.RoutineV2Factory;
import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_backend.user.service.UserManager;
import bitnagil.bitnagil_backend.userOnboardingInfo.domain.UserOnboardingInfo;
import bitnagil.bitnagil_backend.userOnboardingInfo.repository.UserOnboardingInfoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final RecommendedRoutineRepository recommendRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;

    private final RecommendedRoutineManager recommendedRoutineManager;
    private final ChangedRoutineFactory changedRoutineFactory;
    private final UserManager userManager;

    // V2 관련 리포지토리
    // TODO: v2로 전환 시 Rename
    private final RoutineInfoV2Repository routineInfoV2Repository;
    private final RoutineV2Repository routineV2Repository;
    private final UserOnboardingInfoRepository userOnboardingInfoRepository;

    private final RoutineV2Factory routineV2Factory;
    private final RoutineInfoV2Factory routineInfoV2Factory;


    /**
     * 유저와 매칭되는 온보딩 결과를 설정하고, 리턴하는 메서드
     */
    @Transactional
    public CustomResponseDto<OnboardingResponse> startOnboarding(OnboardingRequest request, User user) {
        // 요청에 알맞는 Onboarding 객체를 찾는다.
        Onboarding onboarding = onboardingRepository
            .findByTimeSlotAndEmotionTypeAndRealOutingFrequencyAndTargetOutingFrequency(
                request.getTimeSlot(),
                request.getEmotionType(),
                request.getRealOutingFrequency(),
                request.getTargetOutingFrequency()
        );

        if(onboarding == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE);
        }

        // 회원은 온보딩과의 연관관계를 설정한다.
        User persistedUser = userManager.getPersistedUser(user);
        persistedUser.updateOnboarding(onboarding);

        // 온보딩의 CASE를 통해 추천루틴을 조회한다.
        List<RecommendedRoutineDto> recommendedRoutineDtoList =
            recommendedRoutineManager.recommendRoutinesByEmotionMarble(onboarding.getResultCase());

        OnboardingResponse response = OnboardingResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();

        return CustomResponseDto.from(response);
    }

    /**
     * 유저와 매칭되는 온보딩 결과를 설정하고, 리턴하는 메서드
     * todo: v2로 전환 예정
     */
    @Transactional
    public CustomResponseDto<OnboardingResponse> startOnboardingV2(OnboardingRequestV2 request, User user) {
        // 요청에 알맞는 Onboarding 객체를 찾는다.
        Onboarding onboarding = onboardingRepository
                .findByTimeSlotAndEmotionTypeAndRealOutingFrequencyAndTargetOutingFrequency(
                        request.getTimeSlot(),
                        EmotionType.valueOf(request.getEmotionType().get(0)), // EmotionType은 List로 받지만, 단일값으로 처리
                        request.getRealOutingFrequency(),
                        request.getTargetOutingFrequency()
                );

        if(onboarding == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE);
        }

        // 회원은 온보딩과의 연관관계를 설정한다.
        User persistedUser = userManager.getPersistedUser(user);
        persistedUser.updateOnboarding(onboarding);

        // 회원과 연관된 UserOnboardingInfo 객체를 찾고, 기존에 존재하는 경우 update, 없는 경우 생성한다.
        UserOnboardingInfo userOnboardingInfo = userOnboardingInfoRepository.findByUser(persistedUser);
        if (userOnboardingInfo == null) { // insert
            UserOnboardingInfo newUserOnboardingInfo = UserOnboardingInfo.builder()
                    .user(persistedUser)
                    .timeSlot(request.getTimeSlot())
                    .emotionTypes(request.getEmotionType())
                    .targetOutingFrequency(request.getTargetOutingFrequency())
                    .build();
            userOnboardingInfoRepository.save(newUserOnboardingInfo);
        }else{ // update
            userOnboardingInfo.updateUserOnboardingInfo(
                    request.getTimeSlot(),
                    request.getEmotionType(),
                    request.getTargetOutingFrequency()
            );
        }

        // 온보딩의 CASE를 통해 추천루틴을 조회한다.
        List<RecommendedRoutineDto> recommendedRoutineDtoList =
                recommendedRoutineManager.recommendRoutinesByEmotionMarble(onboarding.getResultCase());

        OnboardingResponse response = OnboardingResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();

        return CustomResponseDto.from(response);
    }


    /**
     * 온보딩 시 추천 루틴을 저장하는 메서드
     */
    @Transactional
    public void registrationRoutinesV2(RegistrationRoutinesRequest request, User user) {

        LocalDate today = LocalDate.now();

        for (Long recommendedRoutineId : request.getRecommendedRoutineIds()) {
            // 추천 루틴을 조회한다
            RecommendedRoutine recommendedRoutine = recommendRoutineRepository.findById(recommendedRoutineId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE));

            // 온보딩으로 등록한 루틴은 루틴 시작, 종료일자가 당일로 설정된다.
            RoutineInfoV2 routineInfo = routineInfoV2Factory.createNewRoutineInfo(
                    recommendedRoutine.getRecommendedRoutineName(),
                    List.of(), // 온보딩은 반복일자를 설정하지 않는다.
                    recommendedRoutine.getExecutionTime(),
                    today,
                    today,
                    recommendedRoutine.getRecommendedRoutineType(),
                    user
            );

            routineInfoV2Repository.save(routineInfo);

            // 추천 서브 루틴을 조회한다.
            List<RecommendedSubRoutine> recommendedSubRoutines =
                    recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);

            // 서브 루틴 이름 리스트 생성
            List<String> subRoutineNames = recommendedSubRoutines.stream()
                    .map(RecommendedSubRoutine::getSubRoutineName)
                    .toList();

            // 서브 루틴 완료 여부 리스트 생성
            List<Boolean> subRoutineCompleteYn = recommendedSubRoutines.stream()
                .map(completeYn -> false)
                .toList();

            // 루틴 정보에 해당하는 루틴을 생성한다.
            RoutineV2 routine = routineV2Factory.createNewRoutine(
                    today,
                    false,
                    subRoutineNames,
                    subRoutineCompleteYn,
                    routineInfo);

            routineV2Repository.save(routine);
        }
    }

    /**
     * 온보딩 시 추천 루틴을 저장하는 메서드
     */
    // TODO: v2로 전환 시 deprecated 처리
    @Deprecated
    @Transactional
    public void registrationRoutines(RegistrationRoutinesRequest request, User user) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<ChangedRoutine> changedRoutines = new ArrayList<>(); // 변경 루틴 리스트
        List<ChangedSubRoutine> changedSubRoutines = new ArrayList<>(); // 변경 세부 루틴 리스트

        for (Long recommendedRoutineId : request.getRecommendedRoutineIds()) {
            // 인자로 전달받은 추천 루틴을 조회한다
            RecommendedRoutine recommendedRoutine = recommendRoutineRepository.findById(recommendedRoutineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE));

            // 온보딩의 추천 루틴 등록은 반복 루틴이 아닌 당일날만 수행되는 루틴이므로 변경루틴 테이블에 저장한다.
            // 원본 루틴이 존재하지 않으므로 원본 루틴 ID는 null로 설정
            ChangedRoutine changedRoutine = changedRoutineFactory.createChangedRoutineForToday(
                user, recommendedRoutine.getRecommendedRoutineName(), recommendedRoutine.getExecutionTime(), today, now);
            changedRoutines.add(changedRoutine);

            List<RecommendedSubRoutine> recommendedSubRoutines =
                recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);

            List<ChangedSubRoutine> subRoutines = IntStream.range(0, recommendedSubRoutines.size())
                    .mapToObj(i -> changedRoutineFactory.createChangedSubRoutineForToday(
                        i, recommendedSubRoutines.get(i).getSubRoutineName(), now, changedRoutine))
                    .toList();

            changedSubRoutines.addAll(subRoutines);
        }

        changedRoutineRepository.saveAll(changedRoutines);
        changedSubRoutineRepository.saveAll(changedSubRoutines);
    }
}
