package bitnagil.bitnagil_backend.routine.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.RoutineRequestBase;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * 루틴에 관련된 서비스 로직을 담은 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final SubRoutineRepository subRoutineRepository;

    // 루틴, 세부루틴을 함께 저장하는 루틴 등록 메서드
    @Transactional
    public void registerRoutine(User user, RegisterRoutineRequest request) {
        Routine routine = saveRoutine(user, request);
        saveSubRoutine(request.getSubRoutineName(), routine);
    }

    // 루틴, 세부 루틴을 수정하는 메서드
    @Transactional
    public void updateRoutine(User user, UpdateRoutineRequest request) {

        Routine routine = validateRoutineOwnership(request.getRoutineId(), user);

        // 기존 루틴, 서브 루틴의 이력 종료일시를 갱신합니다.
        routine.updateHistoryEndDate(TimeUtils.CURRENT_DATE_TIME);
        subRoutineRepository.findByRoutine(routine)
            .forEach(subRoutine -> subRoutine.updateHistoryEndDate(TimeUtils.CURRENT_DATE_TIME));

        // 변경된 루틴, 세부루틴에 대한 새로운 ROW 추가
        Routine updateRoutine = saveRoutine(user, request);
        saveSubRoutine(request.getSubRoutineName(), updateRoutine);
    }

    // 루틴, 세부 루틴을 삭제하는 메서드
    @Transactional
    public void deleteRoutine(User user, Long routineId) {
        Routine routine = validateRoutineOwnership(routineId, user);

        // 기존 루틴, 서브 루틴의 이력 종료일시를 갱신합니다.
        routine.updateHistoryEndDate(TimeUtils.CURRENT_DATE_TIME);
        subRoutineRepository.findByRoutine(routine)
            .forEach(subRoutine -> subRoutine.updateHistoryEndDate(TimeUtils.CURRENT_DATE_TIME));
    }

    // 요청 루틴 ID가 유저가 등록한 루틴인지 검증하는 메서드
    private Routine validateRoutineOwnership(Long routineId, User user) {
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        if (!user.equals(routine.getUser())) {
            throw new CustomException(ErrorCode.ROUTINE_USER_NOT_MATCHED);
        }

        return routine;
    }

    // 루틴을 등록할 때, 수정할 때 모두 사용되는 루틴 저장 메서드
    private Routine saveRoutine(User user, RoutineRequestBase request) {
        Routine routine = Routine.builder()
            .name(request.getRoutineName())
            .repeatDay(request.getRepeatDay())
            .executionTime(request.getExecutionTime())
            .historyStartDate(TimeUtils.CURRENT_DATE_TIME)
            .historyEndDate(TimeUtils.END_DATE_TIME)
            .user(user)
            .build();

        return routineRepository.save(routine);
    }

    private void saveSubRoutine(List<String> subRoutineNames, Routine routine) {
        for (String subRoutineName : subRoutineNames) {
            SubRoutine subRoutine = SubRoutine.builder()
                .name(subRoutineName)
                .historyStartDate(TimeUtils.CURRENT_DATE_TIME)
                .historyEndDate(TimeUtils.END_DATE_TIME)
                .routine(routine)
                .build();

            subRoutineRepository.save(subRoutine);
        }
    }
}
