package bitnagil.bitnagil_backend.routine.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.SubRoutineInfo;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * 루틴, 서브루틴에 관련된 서비스 로직을 담은 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final SubRoutineRepository subRoutineRepository;

    // 루틴, 세부루틴을 함께 저장하는 루틴 등록 메서드
    @Transactional
    public void registerRoutine(User user, RegisterRoutineRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Routine routine = saveRoutine(user, request, now);
        saveSubRoutine(request.getSubRoutineName(), routine, now);
    }

    // 루틴, 세부 루틴을 수정하는 메서드
    @Transactional
    public void updateRoutine(User user, UpdateRoutineRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Routine previousRoutine = validateRoutineOwnership(request.getRoutineId(), user, now);

        if (hasRoutineChanged(request, previousRoutine))  {

            previousRoutine.updateHistoryEndDateTime(now);
            addUpdatedRoutine(user, request, previousRoutine, now);
        }

        // 서브루틴 갱신
        for (SubRoutineInfo subRoutineInfo : request.getSubRoutineInfos()) {

            // 기존 서브루틴 변경 및 유지
            if (subRoutineInfo.getSubRoutineId() != null && subRoutineInfo.getSubRoutineName() != null) {
                SubRoutine previousSubRoutine = subRoutineRepository
                    .findBySubRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
                        subRoutineInfo.getSubRoutineId(), now, now)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SUB_ROUTINE));

                    // 기존 서브루틴의 이름을 변경한 경우 (이력 갱신)
                    if (!subRoutineInfo.getSubRoutineName().equals(previousSubRoutine.getName())) {
                        previousSubRoutine.updateHistoryEndDateTime(now);
                        addUpdatedSubRoutine(subRoutineInfo, previousSubRoutine, now);
                    }
                    // 기존 서브루틴의 이름을 유지하고, 정렬 순서가 변경된 경우
                    if (subRoutineInfo.getSubRoutineName().equals(previousSubRoutine.getName()) &&
                        !previousSubRoutine.getSortOrder().equals(subRoutineInfo.getSortOrder())) {
                        previousSubRoutine.updateSortOrder(subRoutineInfo.getSortOrder());
                }
            }

            // 기존 서브루틴 삭제
            if (subRoutineInfo.getSubRoutineId() != null && subRoutineInfo.getSubRoutineName() == null) {
                SubRoutine removeSubRoutine = subRoutineRepository
                    .findBySubRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
                        subRoutineInfo.getSubRoutineId(), now, now)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SUB_ROUTINE));

                removeSubRoutine.updateHistoryEndDateTime(now);
            }

            // 새로운 서브루틴 추가
            if (subRoutineInfo.getSubRoutineId() == null && subRoutineInfo.getSubRoutineName() != null) {
                SubRoutine newSubRoutine = SubRoutine.builder()
                    .subRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
                    .name(subRoutineInfo.getSubRoutineName())
                    .sortOrder(subRoutineInfo.getSortOrder())
                    .historyStartDateTime(now)
                    .historyEndDateTime(TimeUtils.END_DATE_TIME)
                    .routineId(previousRoutine.getRoutinePk().getId())
                    .build();

                subRoutineRepository.save(newSubRoutine);
            }
        }
    }

    // 루틴, 세부 루틴을 삭제하는 메서드
    @Transactional
    public void deleteRoutine(User user, UUID routineId) {
        LocalDateTime now = LocalDateTime.now();

        Routine routine = validateRoutineOwnership(routineId, user, now);

        // 기존 루틴, 서브 루틴의 이력 종료일시를 갱신합니다.
        routine.updateHistoryEndDateTime(now);

        // 서브 루틴을 순회하면서 이력 종료일시 갱신
        subRoutineRepository.findByRoutineId(routineId)
            .forEach(subRoutine -> subRoutine.updateHistoryEndDateTime(now));
    }

    // 갱신된 서브루틴을 SubRoutine 테이블에 새로운 Row 추가
    private void addUpdatedSubRoutine(SubRoutineInfo subRoutineInfo, SubRoutine previousSubRoutine,
        LocalDateTime now) {
        // 서브루틴을 갱신하여 새로운 Row 추가
        HistoryPk subRoutinePk = new HistoryPk(previousSubRoutine.getSubRoutinePk().getId(),
            previousSubRoutine.getSubRoutinePk().getHistorySeq() + 1);

        SubRoutine updateSubRoutine = SubRoutine.builder()
            .subRoutinePk(subRoutinePk)
            .name(subRoutineInfo.getSubRoutineName())
            .sortOrder(subRoutineInfo.getSortOrder())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .routineId(previousSubRoutine.getRoutineId())
            .build();

        subRoutineRepository.save(updateSubRoutine);
    }

    // 갱신된 루틴을 Routine 테이블에 새로운 Row 추가
    private void addUpdatedRoutine(User user, UpdateRoutineRequest request, Routine previousRoutine,
        LocalDateTime now) {
        // 이전 루틴에 대한 복합 키를 이력 순번만 증가 시켜 생성
        HistoryPk nextRoutinePk = new HistoryPk(previousRoutine.getRoutinePk().getId(),
            previousRoutine.getRoutinePk().getHistorySeq() + 1);

        // 갱신된 컬럼을 검증 및 수정하여 새로운 갱신된 루틴 생성
        Routine updateRoutine = Routine.builder()
            .routinePk(nextRoutinePk)
            .name(previousRoutine.getName().equals(request.getRoutineName()) ?
                previousRoutine.getName() : request.getRoutineName())
            .repeatDay(previousRoutine.getRepeatDay().equals(request.getRepeatDay()) ?
                previousRoutine.getRepeatDay() : request.getRepeatDay())
            .executionTime(previousRoutine.getExecutionTime().equals(request.getExecutionTime()) ?
                previousRoutine.getExecutionTime() : request.getExecutionTime())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .userId(user.getUserPk().getId())
            .build();

        routineRepository.save(updateRoutine);
    }

    // 서브루틴을 제외한 루틴 필드에서 변경된 필드가 있는지 검증
    private boolean hasRoutineChanged(UpdateRoutineRequest request, Routine previousRoutine) {
        return !previousRoutine.getName().equals(request.getRoutineName()) ||
            !previousRoutine.getRepeatDay().equals(request.getRepeatDay()) ||
            !previousRoutine.getExecutionTime().equals(request.getExecutionTime());
    }

    // 요청 루틴 ID가 유저가 등록한 루틴인지 검증하는 메서드
    private Routine validateRoutineOwnership(UUID routineId, User user, LocalDateTime now) {

        Routine routine = routineRepository
            .findByRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
                routineId, now, now)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        if (!user.getUserPk().getId().equals(routine.getUserId())) {
            throw new CustomException(ErrorCode.ROUTINE_USER_NOT_MATCHED);
        }

        return routine;
    }

    // 루틴을 등록할 때, 수정할 때 모두 사용되는 루틴 저장 메서드
    private Routine saveRoutine(User user, RegisterRoutineRequest request, LocalDateTime now) {

        Routine routine = Routine.builder()
            .routinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .name(request.getRoutineName())
            .repeatDay(request.getRepeatDay())
            .executionTime(request.getExecutionTime())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .userId(user.getUserPk().getId())
            .build();

        return routineRepository.save(routine);
    }

    private void saveSubRoutine(List<String> subRoutineNames, Routine routine, LocalDateTime now) {
        int sortOrder = 1;
        for (String subRoutineName : subRoutineNames) {
            SubRoutine subRoutine = SubRoutine.builder()
                .subRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
                .name(subRoutineName)
                .sortOrder(sortOrder++)
                .historyStartDateTime(now)
                .historyEndDateTime(TimeUtils.END_DATE_TIME)
                .routineId(routine.getRoutinePk().getId())
                .build();

            subRoutineRepository.save(subRoutine);
        }
    }
}
