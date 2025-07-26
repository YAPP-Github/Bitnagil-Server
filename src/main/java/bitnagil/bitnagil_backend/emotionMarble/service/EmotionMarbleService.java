package bitnagil.bitnagil_backend.emotionMarble.service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.domain.enums.EmotionMarbleType;
import bitnagil.bitnagil_backend.emotionMarble.repository.EmotionMarbleRepository;
import bitnagil.bitnagil_backend.emotionMarble.request.RegisterEmotionMarbleRequest;
import bitnagil.bitnagil_backend.emotionMarble.response.EmotionMarbleTypeResponse;
import bitnagil.bitnagil_backend.recommendedRoutine.response.RecommendedRoutineDto;
import bitnagil.bitnagil_backend.emotionMarble.response.RegisterEmotionMarbleResponse;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;

import bitnagil.bitnagil_backend.recommendedRoutine.service.RecommendedRoutineService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 감정 구슬에 대한 로직을 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class EmotionMarbleService {

    private final EmotionMarbleRepository emotionMarbleRepository;

    private final RecommendedRoutineService recommendedRoutineService;
    private final EmotionMarbleFactory emotionMarbleFactory;

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

        EmotionMarble emotionMarble = emotionMarbleFactory.createTodayEmotionMarble(
            user, request, nowDate, nowDateTime, endDateTime);

        emotionMarbleRepository.save(emotionMarble);

        // 감정 구슬에 따른 추천 루틴 응답
        List<RecommendedRoutineDto> recommendedRoutineDtoList =
            recommendedRoutineService.recommendRoutinesByEmotionMarble(emotionMarble.getResultCase());

        return RegisterEmotionMarbleResponse.builder()
                .recommendedRoutines(recommendedRoutineDtoList)
                .build();
    }


}
