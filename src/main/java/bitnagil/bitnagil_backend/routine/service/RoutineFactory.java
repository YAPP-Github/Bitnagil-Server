package bitnagil.bitnagil_backend.routine.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_domain.changedRoutine.domain.enums.ChangedDivCode;
import bitnagil.common.entity.HistoryPk;
import bitnagil.bitnagil_backend.global.utils.TimeUtils;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.RoutineCompletion;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.request.DeleteRoutineByDayRequest;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.RoutineCompletionInfo;
import bitnagil.bitnagil_backend.routine.request.SubRoutineInfo;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineCompletionRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_domain.user.domain.User;

/**
 * 루틴 관련 엔티티 생성, 초기화 책임을 담당하는 클래스입니다.
 */
@Component
public class RoutineFactory {

    // 신규 Routine 엔티티 생성 및 초기화
    public Routine createNewRoutine(User user, RegisterRoutineRequest request, LocalDateTime now) {
        return Routine.builder()
            .routinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .name(request.getRoutineName())
            .repeatDay(request.getRepeatDay())
            .executionTime(request.getExecutionTime())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .userId(user.getUserId())
            .build();
    }

    // 신규 SubRoutine 리스트 생성 및 초기화
    public List<SubRoutine> createNewSubRoutines(List<String> subRoutineNames, Routine routine, LocalDateTime now) {
        List<SubRoutine> subRoutines = new ArrayList<>();
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
            subRoutines.add(subRoutine);
        }
        return subRoutines;
    }

    // 갱신용 Routine 엔티티 생성 (이력 순번 증가 포함)
    public Routine addUpdatedRoutine(User user, UpdateRoutineRequest request, Routine previousRoutine,
        LocalDateTime now) {
        // 이전 루틴에 대한 복합 키를 이력 순번만 증가 시켜 생성
        HistoryPk nextRoutinePk = new HistoryPk(previousRoutine.getRoutinePk().getId(),
            previousRoutine.getRoutinePk().getHistorySeq() + 1);

        // 갱신된 컬럼을 검증 및 수정하여 새로운 갱신된 루틴 생성
        return Routine.builder()
            .routinePk(nextRoutinePk)
            .name(previousRoutine.getName().equals(request.getRoutineName()) ?
                previousRoutine.getName() : request.getRoutineName())
            .repeatDay(previousRoutine.getRepeatDay().equals(request.getRepeatDay()) ?
                previousRoutine.getRepeatDay() : request.getRepeatDay())
            .executionTime(previousRoutine.getExecutionTime().equals(request.getExecutionTime()) ?
                previousRoutine.getExecutionTime() : request.getExecutionTime())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .userId(user.getUserId())
            .build();
    }

    // 기존 subRoutineId는 유지하고 이력 순번만 증가한 새로운 엔티티 생성
    public SubRoutine createNextHistorySubRoutine(SubRoutineInfo subRoutineInfo, SubRoutine previousSubRoutine,
        LocalDateTime now) {
        // 서브루틴을 갱신하여 새로운 Row 추가
        HistoryPk subRoutinePk = new HistoryPk(previousSubRoutine.getSubRoutinePk().getId(),
            previousSubRoutine.getSubRoutinePk().getHistorySeq() + 1);

        return SubRoutine.builder()
            .subRoutinePk(subRoutinePk)
            .name(subRoutineInfo.getSubRoutineName())
            .sortOrder(subRoutineInfo.getSortOrder())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .routineId(previousSubRoutine.getRoutineId())
            .build();
    }

    // 루틴을 수정하면서 새로운 서브 루틴을 추가할 때 사용하는 메서드 (수정하는 과정에서 유저가 설정한 순서를 기반으로 sortOrder 주입)
    public SubRoutine createSubRoutineForRoutineUpdate(SubRoutineInfo subRoutineInfo, Routine previousRoutine, LocalDateTime now) {

        return SubRoutine.builder()
            .subRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .name(subRoutineInfo.getSubRoutineName())
            .sortOrder(subRoutineInfo.getSortOrder())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .routineId(previousRoutine.getRoutinePk().getId())
            .build();
    }

    // 선택한 요일(당일)에만 루틴 삭제를 반영하기 위해 ChangedRoutine 엔티티 생성
    public ChangedRoutine createChangedRoutineForDelete(DeleteRoutineByDayRequest request, Routine routine,
        LocalDateTime now) {

        return ChangedRoutine.builder()
            .changedRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .changedRoutineName(routine.getName())
            .changedExecutionTime(routine.getExecutionTime())
            .originalRoutineDate(request.getPerformedDate())
            .changedRoutineDate(request.getPerformedDate())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .changedDivCode(ChangedDivCode.TODAY_DELETE)
            .userId(routine.getUserId())
            .routineId(routine.getRoutinePk().getId())
            .build();
    }

    // 선택한 요일(당일)에만 루틴 삭제를 반영하기 위해 ChangedSubRoutine 엔티티 생성
    public ChangedSubRoutine createChangedSubRoutineForDelete(SubRoutine subRoutine, LocalDateTime now,
        ChangedRoutine changedRoutineForDelete) {

        return ChangedSubRoutine.builder()
            .changedSubRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
            .changedSubRoutineName(subRoutine.getName())
            .historyStartDateTime(now)
            .historyEndDateTime(TimeUtils.END_DATE_TIME)
            .changedRoutineId(changedRoutineForDelete.getChangedRoutinePk().getId())
            .sortOrder(subRoutine.getSortOrder())
            .build();
    }

    // // 유저가 한번도 체크하지 않아서 RoutineCompletion 엔티티가 없는 경우 엔티티 생성
    public RoutineCompletion createRoutineCompletion(UpdateRoutineCompletionRequest request,
        RoutineCompletionInfo routineCompletionInfo) {

        return RoutineCompletion.builder()
            .completeYn(routineCompletionInfo.getCompleteYn())
            .performedDate(request.getPerformedDate())
            .routineId(routineCompletionInfo.getRoutineId())
            .routineHistorySeq(routineCompletionInfo.getHistorySeq())
            .routineType(routineCompletionInfo.getRoutineType())
            .build();
    }
}
