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
import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.emotionMarble.repository.EmotionMarbleRepository;
import bitnagil.bitnagil_backend.routine.domain.RoutineCompletion;
import bitnagil.bitnagil_backend.routine.domain.enums.RoutineType;
import bitnagil.bitnagil_backend.routine.repository.RoutineCompletionRepository;
import bitnagil.bitnagil_backend.routine.request.DeleteRoutineByDayRequest;
import bitnagil.bitnagil_backend.routine.request.RoutineCompletionInfo;
import bitnagil.bitnagil_backend.routine.request.SubRoutineInfoForDelete;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineCompletionRequest;
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
import lombok.extern.slf4j.Slf4j;

/**
 * 루틴, 서브루틴에 관련된 서비스 로직을 담은 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final SubRoutineRepository subRoutineRepository;
    private final ChangedRoutineRepository changedRoutineRepository;
    private final ChangedSubRoutineRepository changedSubRoutineRepository;
    private final RoutineCompletionRepository routineCompletionRepository;
    private final EmotionMarbleRepository emotionMarbleRepository;

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

        // 기존 루틴, 서브 루틴의 이력 종료일시 및 deleteAt 갱신
        routine.updateHistoryEndDateTime(now);
        routine.setDeleteAt(now);

        // 서브 루틴을 순회하면서 이력 종료일시 및 deleteAt 갱신
        subRoutineRepository.findByRoutineId(routineId)
            .forEach(subRoutine -> {
                subRoutine.updateHistoryEndDateTime(now);
                subRoutine.setDeleteAt(now);
            });

    }

    // 유저가 선택한 요일(당일)만 루틴, 서브 루틴을 삭제하는 메서드
    @Transactional
    public void deleteRoutineByDay(User user, DeleteRoutineByDayRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Routine routine = validateRoutineOwnership(request.getRoutineId(), user, now);

        // 변경 루틴으로 전환
        ChangedRoutine changedRoutineForDelete = ChangedRoutine.builder()
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

        changedRoutineRepository.save(changedRoutineForDelete);

        // 루틴, performedDate에 해당하는 완료 여부 데이터 삭제
        deleteRoutineCompletionIfRoutineIdMatches(request.getRoutineCompletionId(), request.getRoutineId());

        // 변경 서브루틴으로 전환
        List<SubRoutine> subRoutines = subRoutineRepository.findByRoutineId(routine.getRoutinePk().getId());

        for (SubRoutine subRoutine : subRoutines) {
            ChangedSubRoutine changedSubRoutineForDelete = ChangedSubRoutine.builder()
                .changedSubRoutinePk(new HistoryPk(UUID.randomUUID(), 1L))
                .changedSubRoutineName(subRoutine.getName())
                .historyStartDateTime(now)
                .historyEndDateTime(TimeUtils.END_DATE_TIME)
                .changedRoutineId(changedRoutineForDelete.getChangedRoutinePk().getId())
                .sortOrder(subRoutine.getSortOrder())
                .build();

            changedSubRoutineRepository.save(changedSubRoutineForDelete);
        }

        // 서브루틴, performedDate에 해당하는 완료 여부 데이터 삭제
        for (SubRoutineInfoForDelete info : request.getSubRoutineInfosForDelete()) {
            deleteRoutineCompletionIfRoutineIdMatches(info.getRoutineCompletionId(), info.getSubRoutineId());
        }
    }

    /**
     * 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 메서드입니다.
     */
    @Transactional(readOnly = true)
    public RoutineSearchResponse getRoutines(User user, LocalDate startDate, LocalDate endDate) {
        return queryRoutines(user, startDate, endDate);
    }

    /**
     * 루틴의 완료 여부를 갱신하는 메서드입니다.
     */
    @Transactional
    public void updateRoutineCompletionStatus(User user, UpdateRoutineCompletionRequest request) {
        List<RoutineCompletionInfo> routineCompletionInfos = request.getRoutineCompletionInfos();

        for (RoutineCompletionInfo routineCompletionInfo : routineCompletionInfos) {

            validateRoutineOwnerShip(user, routineCompletionInfo);

            // 기존 완료 여부 엔티티가 존재하는지 조회
            RoutineCompletion routineCompletion = routineCompletionRepository
                .findByRoutineIdAndRoutineHistorySeqAndRoutineType(
                    routineCompletionInfo.getRoutineId(),
                    routineCompletionInfo.getHistorySeq(),
                    routineCompletionInfo.getRoutineType());

            // 이미 엔티티가 존재하는 경우 완료여부 갱신
            if (routineCompletion != null) {
                RoutineCompletion existingRoutineCompletion = routineCompletion;
                existingRoutineCompletion.updateCompleteYn(routineCompletionInfo.getCompleteYn());
            }
            else { // 유저가 한번도 체크하지 않아서 엔티티가 생기지 않은 경우 엔티티 생성
                RoutineCompletion newRoutineCompletion = RoutineCompletion.builder()
                    .completeYn(routineCompletionInfo.getCompleteYn())
                    .performedDate(request.getPerformedDate())
                    .routineId(routineCompletionInfo.getRoutineId())
                    .routineHistorySeq(routineCompletionInfo.getHistorySeq())
                    .routineType(routineCompletionInfo.getRoutineType())
                    .build();

                routineCompletionRepository.save(newRoutineCompletion);
            }
        }
    }

    // 루틴, performedDate에 해당하는 완료 여부 데이터 삭제
    private void deleteRoutineCompletionIfRoutineIdMatches(Long routineCompletionId, UUID routineId) {

        // 완료 여부가 생성되지 않은 루틴일 경우
        if (routineCompletionId == null) {
            return;
        }

        RoutineCompletion routineCompletion = routineCompletionRepository.findById(routineCompletionId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE_COMPLETION)
        );

        if (!routineCompletion.getRoutineId().equals(routineId)) {
            throw new CustomException(ErrorCode.ROUTINE_ID_MISMATCH);
        }

        routineCompletionRepository.delete(routineCompletion);
    }

    // 각 타입의 루틴이 실제로 존재하는 루틴인지, 실제로 유저가 가지고 있는 루틴인지 검증하는 메서드
    private void validateRoutineOwnerShip(User user, RoutineCompletionInfo routineCompletionInfo) {
        RoutineType routineType = routineCompletionInfo.getRoutineType();
        HistoryPk historyPk = new HistoryPk(routineCompletionInfo.getRoutineId(), routineCompletionInfo.getHistorySeq());

        switch (routineType) {
            case ROUTINE:
                Routine routine = routineRepository.findByRoutinePk(historyPk).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

                if (!user.getUserPk().getId().equals(routine.getUserId())) {
                    throw new CustomException(ErrorCode.ROUTINE_USER_NOT_MATCHED);
                }
                break;

            case SUB_ROUTINE:
                SubRoutine subRoutine = subRoutineRepository.findBySubRoutinePk(historyPk).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_SUB_ROUTINE));

                // 추후 성능 이슈가 발생할 수 있는 부분
                List<Routine> routines = routineRepository.findByRoutinePk_Id(subRoutine.getRoutineId());

                if (!user.getUserPk().getId().equals(routines.get(0).getUserId())) {
                    throw new CustomException(ErrorCode.SUB_ROUTINE_USER_NOT_MATCHED);
                }
                break;

            case CHANGED_ROUTINE:
                ChangedRoutine changedRoutine = changedRoutineRepository.findByChangedRoutinePk(historyPk)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHANGED_ROUTINE));

                if (!user.getUserPk().getId().equals(changedRoutine.getUserId())) {
                    throw new CustomException(ErrorCode.CHANGED_ROUTINE_USER_NOT_MATCHED);
                }
                break;

            case CHANGED_SUB_ROUTINE:
                ChangedSubRoutine changedSubRoutine = changedSubRoutineRepository.findByChangedSubRoutinePk(historyPk)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHANGED_SUB_ROUTINE));

                List<ChangedRoutine> changedRoutines = changedRoutineRepository.findByChangedRoutinePk_Id(
                    changedSubRoutine.getChangedRoutineId());

                if (!user.getUserPk().getId().equals(changedRoutines.get(0).getUserId())) {
                    throw new CustomException(ErrorCode.CHANGED_SUB_ROUTINE_USER_NOT_MATCHED);
                }
                break;
        }
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

    /**
     * 특정 기간(startDate ~ endDate)의 루틴을 조회하는 메서드
     * 루틴과 변경 루틴에 대한 조회를 통해 조회시 변경분에 대한 부분을 반영한다.
     */
    private RoutineSearchResponse queryRoutines(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();

        // 루틴 테이블의 살아있는 이력을 모두 조회한다.
        // todo: 추후 루틴 시작일시와 종료일시가 추가되면 조회 기간안에 루틴 종료일시 혹은 시작일시가 존재하는지를 파악하여 해당 기간내에 존재하는 루틴만 조회하도록 수정이 필요하다.
        List<Routine> routines = routineRepository.findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                user.getUserPk().getId(), now, now
        );

        // 루틴을 날짜별로 묶어서 반환할 Map을 날짜별로 초기화 해놓는다.
        Map<LocalDate, List<RoutineSearchResultDto>> routinesByDateResponse = new HashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            routinesByDateResponse.put(date, new ArrayList<>()); // 현재 날짜의 Map을 초기화

            DayOfWeek currentDayOfWeek = date.getDayOfWeek(); // 현재 날짜의 요일(ex: 2025-07-22 -> TUESDAY)
            // 조회해온 루틴을 순회하면서 현재 날짜의 요일과 루틴의 반복요일이 일치하는 경우 Map에 해당 루틴을 담는다.
            for (Routine routine : routines) {
                List<DayOfWeek> repeatDays = routine.getRepeatDay();

                // 루틴의 반복요일이 현재 날짜의 요일과 일치하는지 확인
                if (repeatDays.contains(currentDayOfWeek)) {
                    // 현재 루틴의 ID를 FK로 가지는 서브루틴 조회
                    List<SubRoutine> subRoutines = subRoutineRepository.findByRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                            routine.getRoutinePk().getId(), now, now
                    );
                    // 서브루틴 List DTO 생성
                    List<SubRoutineSearchResultDto> subRoutineSearchResultList = new ArrayList<>();
                    for (SubRoutine subRoutine : subRoutines) {

                        // 서브 루틴 완료 여부 조회
                        RoutineCompletion subRoutineCompletion = routineCompletionRepository.findByRoutineIdAndRoutineHistorySeqAndRoutineType(
                                subRoutine.getSubRoutinePk().getId(), subRoutine.getSubRoutinePk().getHistorySeq(), RoutineType.SUB_ROUTINE);

                        SubRoutineSearchResultDto subRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                                .subRoutineId(subRoutine.getSubRoutinePk().getId())
                                .historySeq(subRoutine.getSubRoutinePk().getHistorySeq())
                                .subRoutineName(subRoutine.getName())
                                .sortOrder(subRoutine.getSortOrder())
                                .modifiedYn(false) // 서브루틴은 일시적인 수정(당일삭제, 미루기 등)이 아니므로 변경여부를 false로 설정
                                .routineCompletionId(subRoutineCompletion == null ? null : subRoutineCompletion.getRoutineCompletionId())
                                .completeYn(subRoutineCompletion == null ? false : subRoutineCompletion.getCompleteYn())
                                .routineType(RoutineType.SUB_ROUTINE)
                                .build();
                        subRoutineSearchResultList.add(subRoutineSearchResultDto);
                    }

                    // 서브루틴을 sortOrder 순으로 정렬
                    subRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                    // 루틴 완료 여부 조회
                    RoutineCompletion routineCompletion = routineCompletionRepository.findByRoutineIdAndRoutineHistorySeqAndRoutineType(
                            routine.getRoutinePk().getId(), routine.getRoutinePk().getHistorySeq(), RoutineType.ROUTINE);

                    RoutineSearchResultDto routineSearchResultDto = RoutineSearchResultDto.builder()
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
                    routinesByDateResponse.get(date).add(routineSearchResultDto); // map에 현재날짜에 해당하는 루틴을 담는다.
                }
            }
        }

        // 변경 루틴 테이블의 변경된 루틴 날짜가 startDate ~ endDate인 이력을 모두 조회한다.
        List<ChangedRoutine> changedRoutines = changedRoutineRepository.findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqualAndChangedRoutineDateBetween(
                user.getUserPk().getId(), now, now, startDate, endDate
        );

        // 변경 루틴을 하나씩 순회하면서 원본 루틴과 겹치는 날짜가 있다면, 원본 루틴을 Map에서 제거하고, 변경 루틴을 넣는다.(삭제는 제외)
        for (ChangedRoutine changedRoutine : changedRoutines) {
            LocalDate originalRoutineDate = changedRoutine.getOriginalRoutineDate(); // 원본 루틴 수행 날짜
            LocalDate changedRoutineDate = changedRoutine.getChangedRoutineDate(); // 변경 루틴 수행 날짜

            // 1. 먼저 Map에서 원본 루틴 날짜에 해당하는 루틴 목록을 가져온다.
            List<RoutineSearchResultDto> routineListForOriginalDate = routinesByDateResponse.get(originalRoutineDate);
            if (!routineListForOriginalDate.isEmpty()) {
                // 2. 원본 루틴 ID와 변경 루틴의 원본 루틴 ID가 일치하는 루틴을 제거
                routineListForOriginalDate.removeIf(dto -> dto.getRoutineId().equals(changedRoutine.getRoutineId()));
            }

            // 3. 변경 루틴이 삭제 타입이 아니면, 변경루틴 날짜에 변경 루틴을 추가(2번에서 원본 루틴은 제거했음)
            if (changedRoutine.getChangedDivCode() != ChangedDivCode.TODAY_DELETE) {

                // 현재 변경루틴의 ID를 FK로 가지는 변경서브루틴 조회
                List<ChangedSubRoutine> changedSubRoutines = changedSubRoutineRepository.findByChangedRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                        changedRoutine.getChangedRoutinePk().getId(), now, now
                );

                // 변경 서브루틴 List DTO 생성
                List<SubRoutineSearchResultDto> changedSubRoutineSearchResultList = new ArrayList<>();
                for (ChangedSubRoutine changedSubRoutine : changedSubRoutines) {

                    // 변경 서브 루틴완료 여부를 파악
                    RoutineCompletion changedSubRoutineCompletion = routineCompletionRepository.findByRoutineIdAndRoutineHistorySeqAndRoutineType(
                            changedSubRoutine.getChangedSubRoutinePk().getId(), changedSubRoutine.getChangedSubRoutinePk().getHistorySeq(), RoutineType.CHANGED_SUB_ROUTINE);

                    SubRoutineSearchResultDto changedSubRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                            .subRoutineId(changedSubRoutine.getChangedSubRoutinePk().getId())
                            .historySeq(changedSubRoutine.getChangedSubRoutinePk().getHistorySeq())
                            .subRoutineName(changedSubRoutine.getChangedSubRoutineName())
                            .sortOrder(changedSubRoutine.getSortOrder())
                            .modifiedYn(true)
                            .routineCompletionId(changedSubRoutineCompletion == null ? null : changedSubRoutineCompletion.getRoutineCompletionId())
                            .completeYn(changedSubRoutineCompletion == null ? false : changedSubRoutineCompletion.getCompleteYn())
                            .routineType(RoutineType.CHANGED_SUB_ROUTINE)
                            .build();
                    changedSubRoutineSearchResultList.add(changedSubRoutineSearchResultDto);
                }

                // 변경 서브루틴 정렬
                changedSubRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                // 변경루틴 완료여부 조회
                RoutineCompletion changedRoutineCompletion = routineCompletionRepository.findByRoutineIdAndRoutineHistorySeqAndRoutineType(
                        changedRoutine.getChangedRoutinePk().getId(), changedRoutine.getChangedRoutinePk().getHistorySeq(), RoutineType.CHANGED_ROUTINE);

                RoutineSearchResultDto changedRoutineSearchResultDto = RoutineSearchResultDto.builder()
                        .routineId(changedRoutine.getChangedRoutinePk().getId())
                        .historySeq(changedRoutine.getChangedRoutinePk().getHistorySeq())
                        .routineName(changedRoutine.getChangedRoutineName())
//                        .repeatDay(changedRoutine.getRepeatDay()) // 변경 루틴은 반복 요일이 없으므로 주석 처리(추후 2차에서는 이런 변경 루틴에 대해 어떻게 처리할지 고민)
                        .executionTime(changedRoutine.getChangedExecutionTime())
                        .subRoutineSearchResultDto(changedSubRoutineSearchResultList)
                        .modifiedYn(true) // 변경 루틴은 수정 여부가 true
                        .routineCompletionId(changedRoutineCompletion == null ? null : changedRoutineCompletion.getRoutineCompletionId())
                        .completeYn(changedRoutineCompletion == null ? false : changedRoutineCompletion.getCompleteYn())
                        .routineType(RoutineType.CHANGED_ROUTINE)
                        .build();

                routinesByDateResponse.get(changedRoutine.getChangedRoutineDate()).add(changedRoutineSearchResultDto);
            }
        }
        // 루틴(대분류)는 실행 시간순으로 정렬한다. 만약 실행시간이 동일하면 어떻게 정렬할까?
        for(LocalDate key: routinesByDateResponse.keySet()) {
            routinesByDateResponse.get(key).sort((a, b)
                    -> a.getExecutionTime().compareTo(b.getExecutionTime()));
        }

        // 감정구슬 조회
        EmotionMarble emotionMarble = emotionMarbleRepository.findByUserIdAndDateIs(user.getUserPk().getId(), LocalDate.now());
        return RoutineSearchResponse.builder()
                .routines(routinesByDateResponse)
                .emotionMarbleType(emotionMarble == null ? null : emotionMarble.getEmotionMarbleType())
                .nickname(user.getNickname())
                .build();
    }
}

/*  혹시 몰라서 남겨둔 로직
        // 날짜 루프: startDate ~ endDate(date 변수가 현재 날짜)
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek currentDayOfWeek = date.getDayOfWeek(); // 현재 날짜의 요일
            for (Routine routine : routines) {
                List<DayOfWeek> repeatDays = routine.getRepeatDay();

                // 루틴의 반복요일이 현재 날짜의 요일과 일치하는지 확인(일치하는 경우에만 해당 날짜에 루틴을 담는다.)
                if (repeatDays.contains(currentDayOfWeek)) {

                    // 현재 루틴의 ID를 FK로 가지는 서브루틴 조회
                    List<SubRoutine> subRoutines = subRoutineRepository.findByRoutineIdAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                            routine.getRoutinePk().getId(), now, now
                    );

                    // 서브루틴 List DTO 생성
                    List<SubRoutineSearchResultDto> subRoutineSearchResultList = new ArrayList<>();
                    for (SubRoutine subRoutine : subRoutines) {
                        SubRoutineSearchResultDto subRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                                .subRoutineId(subRoutine.getSubRoutinePk().getId())
                                .subRoutineName(subRoutine.getName())
                                .sortOrder(subRoutine.getSortOrder())
                                .build();
                        subRoutineSearchResultList.add(subRoutineSearchResultDto);
                    }

                    // 서브루틴 정렬
                    subRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                    // todo: 완료여부 추가
                    RoutineSearchResultDto routineSearchResultDto = RoutineSearchResultDto.builder()
                            .routineId(routine.getRoutinePk().getId())
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
                                routine.getRoutinePk().getId().equals(changedRoutine.getRoutineId())) {
                            // 변경 루틴의 구분코드를 보니 삭제인 경우에는 방금 담은 루틴을 지운다.
                            if (changedRoutine.getChangedDivCode() == ChangedDivCode.TODAY_DELETE) {
                                routinesByDateResponse.get(date).remove(routineSearchResultDto);
                                break; // 변경된 루틴이 삭제인 경우에는 더 이상 확인할 필요가 없으므로 루프를 빠져나온다.
                            } else { // 변경된 루틴인 경우 원본 루틴 대신 변경 루틴을 담는다.
                                routinesByDateResponse.get(date).remove(routineSearchResultDto);

                                // 현재 변경루틴의 ID를 FK로 가지는 변경서브루틴 조회
                                List<ChangedSubRoutine> changedSubRoutines = changedSubRoutineRepository.findByChangedRoutineIdAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                                        changedRoutine.getRoutineId(), now, now
                                );

                                // 변경 서브루틴 List DTO 생성
                                List<SubRoutineSearchResultDto> changedSubRoutineSearchResultList = new ArrayList<>();
                                for (ChangedSubRoutine changedSubRoutine : changedSubRoutines) {
                                    SubRoutineSearchResultDto changedSubRoutineSearchResultDto = SubRoutineSearchResultDto.builder()
                                            .subRoutineId(changedSubRoutine.getChangedSubRoutinePk().getId())
                                            .subRoutineName(changedSubRoutine.getChangedSubRoutineName())
                                            .sortOrder(changedSubRoutine.getSortOrder())
                                            .build();
                                    changedSubRoutineSearchResultList.add(changedSubRoutineSearchResultDto);
                                }

                                // 변경 서브루틴 정렬
                                changedSubRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                                // todo: 완료여부 추가
                                RoutineSearchResultDto changedRoutineSearchResultDto = RoutineSearchResultDto.builder()
                                        // 원본 루틴 id를 담아야할지 변경 루틴 id를 담아야할지 고민(화면에서 해당 루틴 수정할때, 해당 루틴이 변경루틴인지 아직 변경안된건지를 구분해서 요청해야할거 같아서..)
                                        .routineId(changedRoutine.getChangedRoutinePk().getId())
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
        */
