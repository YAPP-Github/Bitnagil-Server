package bitnagil.bitnagil_backend.onboarding.service;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_backend.changedRoutine.service.ChangedRoutineFactory;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.onboarding.repository.OnboardingRepository;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.request.RegistrationRoutinesRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.service.RecommendedRoutineManager;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RecommendedRoutineRepository recommendRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;


    private final RecommendedRoutineManager recommendedRoutineManager;
    private final ChangedRoutineFactory changedRoutineFactory;

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

        // 회원은 온보딩과의 연관관계를 설정한다.
        user = userRepository.findByUserPk(user.getUserPk()).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_USER));
        user.updateOnboarding(onboarding);

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
            ChangedRoutine changedRoutine = changedRoutineFactory.createChangedRoutineForOnboarding(
                user, recommendedRoutine, today, now);
            changedRoutines.add(changedRoutine);

            List<RecommendedSubRoutine> recommendedSubRoutines =
                recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);

            List<ChangedSubRoutine> subRoutines = IntStream.range(0, recommendedSubRoutines.size())
                    .mapToObj(i -> changedRoutineFactory.createChangedSubRoutineForOnboarding(
                        i, recommendedSubRoutines.get(i), now, changedRoutine))
                    .toList();

            changedSubRoutines.addAll(subRoutines);
        }

        changedRoutineRepository.saveAll(changedRoutines);
        changedSubRoutineRepository.saveAll(changedSubRoutines);
    }
}
