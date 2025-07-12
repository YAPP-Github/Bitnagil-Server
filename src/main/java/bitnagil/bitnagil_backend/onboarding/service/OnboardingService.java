package bitnagil.bitnagil_backend.onboarding.service;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.onboarding.domain.Onboarding;
import bitnagil.bitnagil_backend.onboarding.repository.OnboardingRepository;
import bitnagil.bitnagil_backend.onboarding.request.OnboardingRequest;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final RecommendedRoutineRepository recommendRoutineRepository;

    public CustomResponseDto<OnboardingResponse> startOnboarding(OnboardingRequest onboardingRequest, User user) {
        // 요청에 알맞는 Onboarding 객체를 찾는다.
        Onboarding onboarding = onboardingRepository.findByTimeSlotAndEmotionTypeAndRealOutingFrequencyAndTargetOutingFrequency(
                onboardingRequest.getTimeSlot(),
                onboardingRequest.getEmotionType(),
                onboardingRequest.getRealOutingFrequency(),
                onboardingRequest.getTargetOutingFrequency()
        );

        // 회원은 온보딩과의 연관관계를 설정한다.
        user = userRepository.findById(user.getUserId()).orElseGet(() -> {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        });
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
                        .recommendedRoutineDetailId(recommendedSubRoutine.getRecommendedSubRoutineId())
                        .recommendedRoutineDetailName(recommendedSubRoutine.getRoutineDetailName())
                        .build();
                recommendedRoutineDetailDtoList.add(recommendedRoutineDetailDto);
            }

            // 추천 루틴을 dto로 변환한다.
            RecommendedRoutineDto recommendedRoutineDto = RecommendedRoutineDto.builder()
                    .recommendedRoutineId(recommendedRoutine.getRecommendedRoutineId())
                    .recommendedRoutineName(recommendedRoutine.getRecommendedRoutineName())
                    .routineDescription(recommendedRoutine.getRecommendedRoutineDescription())
                    .recommendedRoutineDetails(recommendedRoutineDetailDtoList) // 세부 루틴은 나중에 추가
                    .build();
            recommendedRoutineDtoList.add(recommendedRoutineDto);
        }

        OnboardingResponse response = OnboardingResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();
        return CustomResponseDto.from(response);
    }
}
