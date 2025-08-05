package bitnagil.bitnagil_backend.emotionMarble.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.request.RegisterEmotionMarbleRequest;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.onboarding.domain.Case;
import bitnagil.bitnagil_backend.user.domain.User;

/**
 * 감정 구슬 관련 엔티티 생성 책임을 담당하는 클래스입니다.
 */
@Component
public class EmotionMarbleFactory {

    // 당일의 감정 구슬을 생성
    public EmotionMarble createTodayEmotionMarble(User user, RegisterEmotionMarbleRequest request, LocalDate nowDate,
        LocalDateTime nowDateTime, LocalDateTime endDateTime) {

        return EmotionMarble.builder()
            .emotionMarblePk(new HistoryPk(UUID.randomUUID(), 1L))
            .emotionMarbleType(request.getEmotionMarbleType())
            .date(nowDate)
            .userId(user.getUserId())
            .historyStartDateTime(nowDateTime)
            .historyEndDateTime(endDateTime) // historyEndDateTime은 당일 11시 59분 59초로 설정(하루씩 설정되기 때문. 이러면 매일 감정 갱신이 불필요함)
            .resultCase( // 감정 구슬에 따른 추천 루틴을 찾기 위해 Case 객체를 생성
                Case.builder()
                    .caseId(request.getEmotionMarbleType().getCaseId())
                    .build()
            ).build();
    }
}
