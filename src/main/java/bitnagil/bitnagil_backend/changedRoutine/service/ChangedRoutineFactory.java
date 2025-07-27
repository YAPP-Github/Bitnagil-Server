package bitnagil.bitnagil_backend.changedRoutine.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.enums.ChangedDivCode;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedRoutine;
import bitnagil.bitnagil_backend.recommendedRoutine.domain.RecommendedSubRoutine;
import bitnagil.bitnagil_backend.user.domain.User;

@Service
public class ChangedRoutineFactory {

    // 유저 초기 온보딩 시 추천 루틴을 등록할 때 변경 루틴에 저장
    public ChangedRoutine createChangedRoutineForOnboarding(
        User user, RecommendedRoutine recommendedRoutine, LocalDate today, LocalDateTime now) {

        return ChangedRoutine.builder()
            .changedRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .changedRoutineName(recommendedRoutine.getRecommendedRoutineName())
            .changedExecutionTime(recommendedRoutine.getTime())
            .originalRoutineDate(today) // 원본 루틴 날짜는 현재 날짜로 설정
            .changedRoutineDate(today) // 변경된 루틴 날짜도 현재 날짜로 설정
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .userId(user.getUserPk().getId())
            .changedDivCode(ChangedDivCode.ONBOARDING)
            .build();
    }

    // 유저 초기 온보딩 시 추천 루틴을 등록할 때 변경 서브루틴에 저장
    public ChangedSubRoutine createChangedSubRoutineForOnboarding(
        int sortOrder, RecommendedSubRoutine recommendedSubRoutine, LocalDateTime now, ChangedRoutine changedRoutine) {

        return ChangedSubRoutine.builder()
            .changedSubRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .changedSubRoutineName(recommendedSubRoutine.getSubRoutineName())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .changedRoutineId(changedRoutine.getChangedRoutinePk().getId())
            .sortOrder(sortOrder + 1) // 1부터 시작
            .build();
    }
}
