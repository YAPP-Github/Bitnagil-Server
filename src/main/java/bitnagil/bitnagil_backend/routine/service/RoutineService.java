package bitnagil.bitnagil_backend.routine.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_backend.changedRoutine.domain.enums.ChangedDivCode;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_backend.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RoutineSearchRequest;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResponse;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResultDto;
import bitnagil.bitnagil_backend.routine.response.SubRoutineSearchResultDto;
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
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;

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

        // 갱신할 서브 루틴이 있는지 탐색 및 갱신 수행
        for (SubRoutineInfo subRoutineInfo : request.getSubRoutineInfos()) {

            SubRoutine previousSubRoutine = subRoutineRepository
                .findBySubRoutinePk_IdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
                    subRoutineInfo.getSubRoutineId(), now, now)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SUB_ROUTINE));

            // 갱신할 서브 루틴명이 null이면 해당 서브 루틴을 삭제
            if (subRoutineInfo.getSubRoutineName() == null) {
                previousSubRoutine.updateHistoryEndDateTime(now);
                continue;
            }

            if (!subRoutineInfo.getSubRoutineName().equals(previousSubRoutine.getName())) {

                previousSubRoutine.updateHistoryEndDateTime(now);
                addUpdatedSubRoutine(subRoutineInfo, previousSubRoutine, now);
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

    /**
     * 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 메서드입니다.
     */
    @Transactional(readOnly = true)
    public RoutineSearchResponse getRoutines(User user, RoutineSearchRequest routineSearchRequest) {
        queryRoutines(user, routineSearchRequest.getStartDate(), routineSearchRequest.getEndDate());
        return null;
    }

    private Routine saveRoutine(User user, RoutineRequest routineRequest) {
    // 갱신된 서브루틴을 SubRoutine 테이블에 새로운 Row 추가
    private void addUpdatedSubRoutine(SubRoutineInfo subRoutineInfo, SubRoutine previousSubRoutine,
        LocalDateTime now) {
        // 서브루틴을 갱신하여 새로운 Row 추가
        HistoryPk nextSubRoutinePk = new HistoryPk(previousSubRoutine.getSubRoutinePk().getId(),
            previousSubRoutine.getSubRoutinePk().getHistorySeq() + 1);

        SubRoutine updateSubRoutine = SubRoutine.builder()
            .subRoutinePk(nextSubRoutinePk)
            .name(subRoutineInfo.getSubRoutineName())
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
            .user(user)
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

        if (!user.getUserPk().equals(routine.getUser().getUserPk())) {
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
            .user(user)
            .build();

        return routineRepository.save(routine);
    }

    private void saveSubRoutine(List<String> subRoutineNames, Routine routine, LocalDateTime now) {
        for (String subRoutineName : subRoutineNames) {
            SubRoutine subRoutine = SubRoutine.builder()
                .subRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
                .name(subRoutineName)
                .historyStartDateTime(now)
                .historyEndDateTime(TimeUtils.END_DATE_TIME)
                .routineId(routine.getRoutinePk().getId())
                .build();

            subRoutineRepository.save(subRoutine);
        }
    }

    /**
     * 특정 기간(startDate ~ endDate)의 루틴을 조회하는 메서드
     * 루틴과 변경 루틴에 대한 조회를 통해 조회시 변경분에 대한 부분을 반영한다.
     */
    private RoutineSearchResponse queryRoutines(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();

        // 루틴 테이블의 살아있는 이력을 모두 조회한다.
        List<Routine> routines = routineRepository.findByUserAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
                user, now, now
        );

        // 변경 루틴 테이블의 변경된 루틴 날짜가 startDate ~ endDate인 살아있는 이력을 모두 조회한다.
        List<ChangedRoutine> changedRoutines = changedRoutineRepository.findByUserAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqualAndChangedRoutineDateBetween(
                user, now, now, startDate, endDate
        );

        // 루틴을 날짜별로 묶어서 반환할 Map을 날짜별로 초기화 해놓는다.
        Map<LocalDate, List<RoutineSearchResultDto>> routinesByDateResponse = new HashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            routinesByDateResponse.put(date, new ArrayList<>());
        }

        // 날짜 루프: startDate ~ endDate(date 변수가 현재 날짜)
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek currentDayOfWeek = date.getDayOfWeek(); // 현재 날짜의 요일
            for (Routine routine : routines) {
                List<DayOfWeek> repeatDays = routine.getRepeatDay();

                // 루틴의 반복요일이 현재 날짜의 요일과 일치하는지 확인(일치하는 경우에만 해당 날짜에 루틴을 담는다.)
                if (repeatDays.contains(currentDayOfWeek)) {

                    // 현재 루틴의 ID를 FK로 가지는 서브루틴 조회
                    List<SubRoutine> subRoutines = subRoutineRepository.findByRoutineAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
                            routine, now, now
                    );

                    // 서브루틴 List DTO 생성
                    List<SubRoutineSearchResultDto> subRoutineSearchResultList = new ArrayList<>();
                    for (SubRoutine subRoutine : subRoutines) {
                        SubRoutineSearchResultDto subRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                                .subRoutineId(subRoutine.getSubRoutineId())
                                .subRoutineName(subRoutine.getName())
                                .build();
                        subRoutineSearchResultList.add(subRoutineSearchResultDto);
                    }

                    // todo: 완료여부 추가
                    RoutineSearchResultDto routineSearchResultDto = RoutineSearchResultDto.builder()
                            .routineId(routine.getRoutineId())
                            .routineName(routine.getName())
                            .executionTime(routine.getExecutionTime())
                            .subRoutineSearchResultDto(subRoutineSearchResultList) // 서브루틴은 나중에 추가
                            .modifiedYn(false) // 기존의 반복 루틴은 수정 여부가 false
                            .build();
                    routinesByDateResponse.get(date).add(routineSearchResultDto); // map에 현재날짜에 해당하는 루틴을 담는다.

                    // 변경루틴을 확인해서 만약 방금 담은 루틴이 변경된 루틴이라면(삭제, 미루기, 변경 등)
                    for (ChangedRoutine changedRoutine : changedRoutines) {
                        // 변경루틴의 원본 날짜가 현재 날짜에 해당하고, 현재 루틴의 ID와 변경 루틴의 원본 루틴 ID가 일치하는 경우
                        if (date.isEqual(changedRoutine.getOriginalRoutineDate()) &&
                                routine.getRoutineId().equals(changedRoutine.getRoutine().getRoutineId())) {
                            // 변경 루틴의 구분코드를 보니 삭제인 경우에는 방금 담은 루틴을 지운다.
                            if (changedRoutine.getChangedDivCode() == ChangedDivCode.TODAY_DELETE) {
                                routinesByDateResponse.get(date).remove(routineSearchResultDto);
                                break; // 변경된 루틴이 삭제인 경우에는 더 이상 확인할 필요가 없으므로 루프를 빠져나온다.
                            } else { // 변경된 루틴인 경우 원본 루틴 대신 변경 루틴을 담는다.
                                routinesByDateResponse.get(date).remove(routineSearchResultDto);

                                // 현재 변경루틴의 ID를 FK로 가지는 변경서브루틴 조회
                                List<ChangedSubRoutine> changedSubRoutines = changedSubRoutineRepository.findByChangedRoutineAndHistoryStartDateBeforeAndHistoryEndDateGreaterThanEqual(
                                        changedRoutine, now, now
                                );

                                // 변경 서브루틴 List DTO 생성
                                List<SubRoutineSearchResultDto> changedSubRoutineSearchResultList = new ArrayList<>();
                                for (ChangedSubRoutine changedSubRoutine : changedSubRoutines) {
                                    SubRoutineSearchResultDto changedSubRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                                            .subRoutineId(changedSubRoutine.getChangedSubRoutineId())
                                            .subRoutineName(changedSubRoutine.getChangedSubRoutineName())
                                            .build();
                                    changedSubRoutineSearchResultList.add(changedSubRoutineSearchResultDto);
                                }

                                // todo: 완료여부 추가
                                RoutineSearchResultDto changedRoutineSearchResultDto = RoutineSearchResultDto.builder()
                                        // 원본 루틴 id를 담아야할지 변경 루틴 id를 담아야할지 고민(화면에서 해당 루틴 수정할때, 해당 루틴이 변경루틴인지 아직 변경안된건지를 구분해서 요청해야할거 같아서..)
                                        .routineId(changedRoutine.getChangedRoutineId())
//                                        .hisseq()
                                        .routineName(changedRoutine.getChangedRoutineName())
                                        .executionTime(changedRoutine.getChangedExecutionTime())
                                        .subRoutineSearchResultDto(changedSubRoutineSearchResultList) // 서브루틴은 나중에 추가
                                        .modifiedYn(true) // 변경 루틴은 수정 여부가 true
                                        .build();
                                routinesByDateResponse.get(changedRoutine.getChangedRoutineDate()).add(changedRoutineSearchResultDto);
                                break; // 변경된 루틴이 있는 경우에는 더 이상 확인할 필요가 없으므로 루프를 빠져나온다.
                            }
                        }
                    }
                }
            }

            // 루틴(대분류)는 실행 시간순으로 정렬한다.
            for(LocalDate key: routinesByDateResponse.keySet()) {
                routinesByDateResponse.get(key).sort((a, b)
                        -> a.getExecutionTime().compareTo(b.getExecutionTime()));
            }
        }
        return RoutineSearchResponse.builder().routines(routinesByDateResponse).build();
    }
}
