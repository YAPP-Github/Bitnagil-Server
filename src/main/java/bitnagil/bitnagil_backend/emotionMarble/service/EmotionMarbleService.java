package bitnagil.bitnagil_backend.emotionMarble.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.emotionMarble.repository.EmotionMarbleRepository;
import bitnagil.bitnagil_backend.emotionMarble.request.RegisterEmotionMarbleRequest;
import bitnagil.bitnagil_backend.emotionMarble.response.EmotionMarbleTypeResponse;
import bitnagil.bitnagil_backend.emotionMarble.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.emotionMarble.response.RecommendedSubRoutineDto;
import bitnagil.bitnagil_backend.emotionMarble.response.RegisterEmotionMarbleResponse;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;

import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedRoutineRepository;
import bitnagil.bitnagil_backend.recommendedRoutine.repository.RecommendedSubRoutineRepository;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmotionMarbleService {

    private final EmotionMarbleRepository emotionMarbleRepository;
    private final RecommendedRoutineRepository recommendRoutineRepository;
    private final RecommendedSubRoutineRepository recommendedSubRoutineRepository;

    // 감정 구술 조회(enum의 value를 가져온다.)
    public EmotionMarbleTypeResponse getEmotionMarbles() {
        EmotionMarbleType[] values = EmotionMarbleType.values();
        return EmotionMarbleTypeResponse.builder().emotionMarbleTypes(values).build();
    }

    // 감정 구슬 등록(1일 1회)
    @Transactional
    public RegisterEmotionMarbleResponse registryEmotionMarble(User user, RegisterEmotionMarbleRequest request) {
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.of(nowDate, LocalTime.of(23, 59, 59));

        // 감정구슬은 1일 1회만 선택할 수 있으므로, 존재 여부를 확인한다.
        if (emotionMarbleRepository.existsByUserIdAndDate(user.getUserPk().getId(), nowDate)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMOTION_MARBLE);
        }

        EmotionMarble emotionMarble = EmotionMarble.builder()
                .emotionMarblePk(new HistoryPk(UUID.randomUUID(), 1L))
                .emotionMarbleType(request.getEmotionMarbleType())
                .date(nowDate)
                .userId(user.getUserPk().getId())
                .historyStartDateTime(nowDateTime)
                .historyEndDateTime(endDateTime) // historyEndDateTime은 당일 11시 59분 59초로 설정(하루씩 설정되기 때문. 이러면 매일 감정 갱신이 불필요함)
                .resultCase( // 감정 구슬에 따른 추천 루틴을 찾기 위해 Case 객체를 생성
                        Case.builder()
                                .caseId(request.getEmotionMarbleType().getCaseId())
                                .build()
                ).build();

        emotionMarbleRepository.save(emotionMarble);

        // 감정 구슬에 따른 추천 루틴 응답
        List<RecommendedRoutine> recommendedRoutines = recommendRoutineRepository.findByResultCase(emotionMarble.getResultCase());
        if (recommendedRoutines.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_RECOMMENDED_ROUTINE);
        }

        // 응답 생성
        List<RecommendedRoutineDto> recommendedRoutineDtoList = new ArrayList<>();
        for (RecommendedRoutine recommendedRoutine : recommendedRoutines) {
            List<RecommendedSubRoutineDto> recommendedRoutineDetailDtoList = new ArrayList<>();

            List<RecommendedSubRoutine> recommendedSubRoutines = recommendedSubRoutineRepository.findByRecommendedRoutine(recommendedRoutine);

            // 추천 루틴의 세부 루틴을 dto로 변환한다.
            for (RecommendedSubRoutine recommendedSubRoutine : recommendedSubRoutines) {
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
                    .recommendedSubRoutines(recommendedRoutineDetailDtoList)
                    .build();
            recommendedRoutineDtoList.add(recommendedRoutineDto);
        }

        RegisterEmotionMarbleResponse response = RegisterEmotionMarbleResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();
        return response;
    }
}
