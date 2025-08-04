package bitnagil.bitnagil_backend.routine.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RoutineCompletionInfo;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 관련된 검증 로직을 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RoutineValidator {

    private final RoutineRepository routineRepository;
    private final SubRoutineRepository subRoutineRepository;
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;

    // 각 타입의 루틴이 실제로 존재하는 루틴인지, 실제로 유저가 가지고 있는 루틴인지 검증하는 메서드
    public void validateRoutineOwnership(User user, RoutineCompletionInfo info) {
        switch (info.getRoutineType()) {
            case ROUTINE:
                validateRoutine(user, info);
                break;
            case SUB_ROUTINE:
                validateSubRoutine(user, info);
                break;
            case CHANGED_ROUTINE:
                validateChangedRoutine(user, info);
                break;
            case CHANGED_SUB_ROUTINE:
                validateChangedSubRoutine(user, info);
                break;
        }
    }

    // 요청 루틴 ID가 유저가 등록한 루틴인지 검증하는 메서드
    public Routine validateRoutineOwnership(UUID routineId, User user, LocalDateTime now) {

        Routine routine = routineRepository
            .findByRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
                routineId, now, now)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        if (!user.getUserId().equals(routine.getUserId())) {
            throw new CustomException(ErrorCode.ROUTINE_USER_NOT_MATCHED);
        }

        return routine;
    }

    private void validateRoutine(User user, RoutineCompletionInfo info) {
        Routine routine = routineRepository
            .findByRoutinePk(new HistoryPk(info.getRoutineId(), info.getHistorySeq()))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        if (!user.getUserId().equals(routine.getUserId())) {
            throw new CustomException(ErrorCode.ROUTINE_USER_NOT_MATCHED);
        }
    }

    private void validateSubRoutine(User user, RoutineCompletionInfo info) {
        SubRoutine subRoutine = subRoutineRepository
            .findBySubRoutinePk(new HistoryPk(info.getRoutineId(), info.getHistorySeq()))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SUB_ROUTINE));

        // 추후 성능 이슈가 발생할 수 있는 부분
        List<Routine> routines = routineRepository.findByRoutinePk_Id(subRoutine.getRoutineId());

        if (!user.getUserId().equals(routines.get(0).getUserId())) {
            throw new CustomException(ErrorCode.SUB_ROUTINE_USER_NOT_MATCHED);
        }
    }

    private void validateChangedRoutine(User user, RoutineCompletionInfo info) {
        ChangedRoutine changedRoutine = changedRoutineRepository
            .findByChangedRoutinePk(new HistoryPk(info.getRoutineId(), info.getHistorySeq()))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHANGED_ROUTINE));

        if (!user.getUserId().equals(changedRoutine.getUserId())) {
            throw new CustomException(ErrorCode.CHANGED_ROUTINE_USER_NOT_MATCHED);
        }
    }

    private void validateChangedSubRoutine(User user, RoutineCompletionInfo info) {
        ChangedSubRoutine changedSubRoutine = changedSubRoutineRepository
            .findByChangedSubRoutinePk(new HistoryPk(info.getRoutineId(), info.getHistorySeq()))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHANGED_SUB_ROUTINE));

        List<ChangedRoutine> changedRoutines = changedRoutineRepository.findByChangedRoutinePk_Id(
            changedSubRoutine.getChangedRoutineId());

        if (!user.getUserId().equals(changedRoutines.get(0).getUserId())) {
            throw new CustomException(ErrorCode.CHANGED_SUB_ROUTINE_USER_NOT_MATCHED);
        }
    }



}
