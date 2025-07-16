package bitnagil.bitnagil_backend.onboarding.service;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.onboarding.repository.OnboardingRepository;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
import bitnagil.bitnagil_backend.onboarding.request.RegistrationRoutinesRequest;
import bitnagil.bitnagil_backend.onboarding.response.OnboardingResponse;
import bitnagil.bitnagil_backend.onboarding.response.RecommendedSubRoutineDto;
import bitnagil.bitnagil_backend.onboarding.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final RecommendedRoutineRepository recommendRoutineRepository;
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;

    /**
     * 유저와 매칭되는 온보딩 결과를 설정하고, 리턴하는 메서드
     */
    @Transactional
    public CustomResponseDto<OnboardingResponse> startOnboarding(OnboardingRequest request, User user) {
        // 요청에 알맞는 Onboarding 객체를 찾는다.
        Onboarding onboarding = onboardingRepository.findByTimeSlotAndEmotionTypeAndRealOutingFrequencyAndTargetOutingFrequency(
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
        List<RecommendedRoutine> recommendedRoutines = recommendRoutineRepository.findByResultCase(onboarding.getResultCase());
        if (recommendedRoutines.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE);
        }

        // 응답 생성
        List<RecommendedRoutineDto> recommendedRoutineDtoList = new ArrayList<>();
        for (RecommendedRoutine recommendedRoutine : recommendedRoutines) {
            List<RecommendedSubRoutineDto> recommendedRoutineDetailDtoList = new ArrayList<>();

            // 추천 루틴의 세부 루틴을 dto로 변환한다.
            for (RecommendedSubRoutine recommendedSubRoutine : recommendedRoutine.getRecommendedSubRoutines()) {
                RecommendedSubRoutineDto recommendedRoutineDetailDto = RecommendedSubRoutineDto.builder()
                        .recommendedSubRoutineId(recommendedSubRoutine.getRecommendedSubRoutineId())
                        .recommendedSubRoutineName(recommendedSubRoutine.getSubRoutineName())
                        .build();
                recommendedRoutineDetailDtoList.add(recommendedRoutineDetailDto);
            }

            // 추천 루틴을 dto로 변환한다.
            RecommendedRoutineDto recommendedRoutineDto = RecommendedRoutineDto.builder()
                    .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
                    .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
                    .routineDescription(recommendedRoutine.getRecommendedRoutineDescription())
                    .recommendedSubRoutines(recommendedRoutineDetailDtoList) // 세부 루틴은 나중에 추가
                    .build();
            recommendedRoutineDtoList.add(recommendedRoutineDto);
        }

        OnboardingResponse response = OnboardingResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();
        return CustomResponseDto.from(response);
    }

    /**
     * 온보딩 시 추천 등록을 저장하는 메서드
     */
    @Transactional
    public void registrationRoutines(RegistrationRoutinesRequest request, User user) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<ChangedRoutine> changedRoutines = new ArrayList<>(); // 변경 루틴 리스트
        List<ChangedSubRoutine> changedSubRoutines = new ArrayList<>(); // 변경 세부 루틴 리스트

        for (Long routineId : request.getRecommendedRoutineIds()) {
            // 인자로 전달받은 추천 루틴을 조회한다
            RecommendedRoutine recommendRoutine = recommendRoutineRepository.findById(routineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE));

            // 온보딩의 추천 루틴 등록은 반복 루틴이 아닌 당일날만 수행되는 루틴이므로 변경루틴 테이블에 저장한다.
            // 원본 루틴이 존재하지 않으므로 원본 루틴 ID는 null로 설정
            ChangedRoutine changedRoutine = ChangedRoutine.builder()
                    .changedRoutineName(recommendRoutine.getRecommendedRoutineName())
                    .changedExecutionTime(recommendRoutine.getTime())
                    .originalRoutineDate(today) // 원본 루틴 날짜는 현재 날짜로 설정
                    .changedRoutineDate(today) // 변경된 루틴 날짜도 현재 날짜로 설정
                    .historyStartDateTime(now)
                    .historyEndDateTime(TimeUtils.END_DATE_TIME)
                    .user(user)
                    .build();
            changedRoutines.add(changedRoutine);

            List<ChangedSubRoutine> subRoutines = recommendRoutine.getRecommendedSubRoutines().stream()
                    .map(sub -> ChangedSubRoutine.builder()
                        .changedSubRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
                        .changedSubRoutineName(sub.getSubRoutineName())
                        .historyStartDateTime(now)
                        .historyEndDateTime(TimeUtils.END_DATE_TIME)
                        .build())
                    .toList();
            changedSubRoutines.addAll(subRoutines);
        }

        changedRoutineRepository.saveAll(changedRoutines);
        changedSubRoutineRepository.saveAll(changedSubRoutines);
    }
}
