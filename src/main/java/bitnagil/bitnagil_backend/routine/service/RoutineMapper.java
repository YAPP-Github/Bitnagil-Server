package bitnagil.bitnagil_backend.routine.service;

import java.util.List;

import org.springframework.stereotype.Service;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.RoutineCompletion;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResultDto;
import bitnagil.bitnagil_backend.routine.response.SubRoutineSearchResultDto;

/**
 * DTO로 변환하는 Mapper 클래스입니다.
 */
@Service
public class RoutineMapper {

    public RoutineSearchResultDto toRoutineSearchResultDto(Routine routine,
        List<SubRoutineSearchResultDto> subRoutineSearchResultList, RoutineCompletion routineCompletion) {

        return RoutineSearchResultDto.builder()
            .routineId(routine.getRoutinePk().getId())
            .historySeq(routine.getRoutinePk().getHistorySeq())
            .routineName(routine.getName())
            .repeatDay(routine.getRepeatDay())
            .executionTime(routine.getExecutionTime())
            .subRoutineSearchResultDto(subRoutineSearchResultList)
            .modifiedYn(false) // 루틴은 일시적인 수정(당일삭제, 미루기 등)이 아니므로 변경여부를 false로 설정
            .routineCompletionId(routineCompletion == null ? null : routineCompletion.getRoutineCompletionId())
            .completeYn(routineCompletion == null ? false : routineCompletion.getCompleteYn())
            .routineType(RoutineType.ROUTINE)
            .build();
    }

    public SubRoutineSearchResultDto toSubRoutineSearchResultDto(
        SubRoutine subRoutine, RoutineCompletion subRoutineCompletion) {

        return SubRoutineSearchResultDto.builder()
            .subRoutineId(subRoutine.getSubRoutinePk().getId())
            .historySeq(subRoutine.getSubRoutinePk().getHistorySeq())
            .subRoutineName(subRoutine.getName())
            .sortOrder(subRoutine.getSortOrder())
            .modifiedYn(false) // 서브루틴은 일시적인 수정(당일삭제, 미루기 등)이 아니므로 변경여부를 false로 설정
            .routineCompletionId(subRoutineCompletion == null ? null : subRoutineCompletion.getRoutineCompletionId())
            .completeYn(subRoutineCompletion == null ? false : subRoutineCompletion.getCompleteYn())
            .routineType(RoutineType.SUB_ROUTINE)
            .build();
    }

    public RoutineSearchResultDto toChangedRoutineSearchResultDto(ChangedRoutine changedRoutine,
        List<SubRoutineSearchResultDto> changedSubRoutineSearchResultList, RoutineCompletion changedRoutineCompletion) {

        return RoutineSearchResultDto.builder()
            .routineId(changedRoutine.getChangedRoutinePk().getId())
            .historySeq(changedRoutine.getChangedRoutinePk().getHistorySeq())
            .routineName(changedRoutine.getChangedRoutineName())
            //.repeatDay(changedRoutine.getRepeatDay()) // 변경 루틴은 반복 요일이 없으므로 주석 처리(추후 2차에서는 이런 변경 루틴에 대해 어떻게 처리할지 고민)
            .executionTime(changedRoutine.getChangedExecutionTime())
            .subRoutineSearchResultDto(changedSubRoutineSearchResultList)
            .modifiedYn(true) // 변경 루틴은 수정 여부가 true
            .routineCompletionId(changedRoutineCompletion == null ? null : changedRoutineCompletion.getRoutineCompletionId())
            .completeYn(changedRoutineCompletion == null ? false : changedRoutineCompletion.getCompleteYn())
            .routineType(RoutineType.CHANGED_ROUTINE)
            .build();
    }

    public SubRoutineSearchResultDto toChangedSubRoutineSearchResultDto(
        ChangedSubRoutine changedSubRoutine, RoutineCompletion changedSubRoutineCompletion) {

        return SubRoutineSearchResultDto.builder()
            .subRoutineId(changedSubRoutine.getChangedSubRoutinePk().getId())
            .historySeq(changedSubRoutine.getChangedSubRoutinePk().getHistorySeq())
            .subRoutineName(changedSubRoutine.getChangedSubRoutineName())
            .sortOrder(changedSubRoutine.getSortOrder())
            .modifiedYn(true)
            .routineCompletionId(changedSubRoutineCompletion == null ? null : changedSubRoutineCompletion.getRoutineCompletionId())
            .completeYn(changedSubRoutineCompletion == null ? false : changedSubRoutineCompletion.getCompleteYn())
            .routineType(RoutineType.CHANGED_SUB_ROUTINE)
            .build();
    }
}
