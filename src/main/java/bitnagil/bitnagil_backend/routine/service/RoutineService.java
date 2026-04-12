package bitnagil.bitnagil_backend.routine.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedRoutine;
import bitnagil.bitnagil_domain.changedRoutine.domain.ChangedSubRoutine;
import bitnagil.bitnagil_domain.changedRoutine.domain.enums.ChangedDivCode;
import bitnagil.bitnagil_domain.changedRoutine.repository.ChangedRoutineRepository;
import bitnagil.bitnagil_domain.changedRoutine.repository.ChangedSubRoutineRepository;
import bitnagil.bitnagil_domain.changedRoutine.service.ChangedRoutineFactory;
import bitnagil.bitnagil_domain.emotionMarble.repository.EmotionMarbleRepository;
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

import bitnagil.common.errorcode.ErrorCode;
import bitnagil.common.exception.CustomException;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.SubRoutineInfo;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_domain.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_domain.routineInfoV2.repository.RoutineInfoV2Repository;
import bitnagil.bitnagil_domain.rountineV2.domain.RoutineV2;
import bitnagil.bitnagil_domain.rountineV2.repository.RoutineV2Repository;
import bitnagil.bitnagil_domain.user.domain.User;
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

    private final RoutineValidator routineValidator;
    private final RoutineFactory routineFactory;
    private final RoutineMapper routineMapper;
    private final ChangedRoutineFactory changedRoutineFactory;
    private final RoutineV2Repository routineV2Repository;
    private final RoutineInfoV2Repository routineInfoV2Repository;

    // 루틴, 세부루틴을 함께 저장하는 루틴 등록 메서드
    @Transactional
    public void registerRoutine(User user, RegisterRoutineRequest request) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 당일 루틴으로 등록한 경우
        if (request.getRepeatDay().isEmpty()) {
            // 당일 루틴을 ChangedRoutine에 등록
            ChangedRoutine changedRoutineForToday = changedRoutineFactory.createChangedRoutineForToday(
                user, request.getRoutineName(), request.getExecutionTime(), today, now);

            changedRoutineRepository.save(changedRoutineForToday);

            // 당일 서브루틴을 ChangedSubRoutine에 등록
            List<ChangedSubRoutine> changedSubRoutines = IntStream.range(0, request.getSubRoutineName().size())
                .mapToObj(i -> changedRoutineFactory.createChangedSubRoutineForToday(
                    i, request.getSubRoutineName().get(i), now, changedRoutineForToday))
                .toList();

            changedSubRoutineRepository.saveAll(changedSubRoutines);
        }
        else { // 반복 요일이 있는 반복 루틴의 경우
            // 루틴 생성 및 저장
            Routine newRoutine = routineFactory.createNewRoutine(user, request, now);
            routineRepository.save(newRoutine);

            // 서브 루틴 생성 및 저장
            List<SubRoutine> newSubRoutines = routineFactory
                .createNewSubRoutines(request.getSubRoutineName(), newRoutine, now);
            subRoutineRepository.saveAll(newSubRoutines);
        }
    }

    // 루틴, 세부 루틴을 수정하는 메서드
    @Transactional
    public void updateRoutine(User user, UpdateRoutineRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Routine previousRoutine = routineValidator.validateRoutineOwnership(request.getRoutineId(), user, now);

        // 루틴에서 변경된 필드가 있는지 검증
        if (previousRoutine.hasRoutineChanged(request))  {
            previousRoutine.updateHistoryEndDateTime(now);
            Routine routine = routineFactory.addUpdatedRoutine(user, request, previousRoutine, now);
            routineRepository.save(routine);
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
                        // 기존 subRoutineId는 유지하고 이력 순번만 증가
                        SubRoutine subRoutine = routineFactory.createNextHistorySubRoutine(
                            subRoutineInfo, previousSubRoutine, now);

                        subRoutineRepository.save(subRoutine);
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

            // 루틴을 수정하면서 새로운 서브 루틴을 추가하는 경우
            if (subRoutineInfo.getSubRoutineId() == null && subRoutineInfo.getSubRoutineName() != null) {
                SubRoutine newSubRoutine = routineFactory.createSubRoutineForRoutineUpdate(subRoutineInfo, previousRoutine, now);
                subRoutineRepository.save(newSubRoutine);
            }
        }
    }

    // 루틴을 모든 날짜에서 삭제하는 메서드
    @Transactional
    public void deleteRoutine(User user, String routineId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (routineId.length() == 36) { // (v1) routineId의 타입이 UUID인 경우
            UUID v1RoutineId = UUID.fromString(routineId);
            deleteV1Routine(user, v1RoutineId, now);
        }
        else { // (v2) routineId의 타입이 Long인 경우
            Long v2RoutineId = Long.valueOf(routineId);
            deleteV2Routine(user, v2RoutineId, today);
        }
    }

    // 유저가 선택한 요일(당일)만 루틴, 서브 루틴을 삭제하는 메서드
    @Transactional
    public void deleteRoutineByDay(User user, DeleteRoutineByDayRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getRoutineType() == RoutineType.ROUTINE) {
            Routine routine = routineValidator.validateRoutine(user, request.getRoutineId(), request.getHistorySeq());

            // 변경 루틴으로 전환
            ChangedRoutine changedRoutineForDelete = routineFactory.createChangedRoutineForDelete(request, routine, now);
            changedRoutineRepository.save(changedRoutineForDelete);

            List<SubRoutine> subRoutines = subRoutineRepository.findByRoutineId(routine.getRoutinePk().getId());

            // 변경 서브루틴으로 전환
            for (SubRoutine subRoutine : subRoutines) {
                ChangedSubRoutine changedSubRoutineForDelete =
                    routineFactory.createChangedSubRoutineForDelete(subRoutine, now, changedRoutineForDelete);
                changedSubRoutineRepository.save(changedSubRoutineForDelete);
            }
        }
        else if (request.getRoutineType() == RoutineType.CHANGED_ROUTINE) {
            ChangedRoutine changedRoutine = routineValidator.validateChangedRoutine(user, request.getRoutineId(),
                request.getHistorySeq());

            // 기존 변경 루틴의 결정 코드를 "오늘만 루틴 삭제"로 변경
            changedRoutine.updateChangedDivCode(ChangedDivCode.TODAY_DELETE);
        }

        // routineCompletionId에 해당하는 루틴 완료 여부 데이터 삭제
        deleteRoutineCompletionIfRoutineIdMatches(request.getRoutineCompletionId(), request.getRoutineId());

        // routineCompletionId에 해당하는 서브 루틴 완료 여부 데이터 삭제
        for (SubRoutineInfoForDelete info : request.getSubRoutineInfosForDelete()) {
            deleteRoutineCompletionIfRoutineIdMatches(info.getRoutineCompletionId(), info.getSubRoutineId());
        }
    }

    // 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 메서드입니다.
    @Transactional(readOnly = true)
    public RoutineSearchResponse getRoutines(User user, LocalDate startDate, LocalDate endDate) {
        return queryRoutines(user, startDate, endDate);
    }

    // TODO: 추후에 어떤 루틴인지 식별이 필요할 때 RoutineType 추가 요망
    // routineId에 대한 단일 루틴 조회하는 메서드입니다.
    @Transactional(readOnly = true)
    public RoutineSearchResultDto getRoutine(User user, UUID routineId) {
        LocalDateTime now = LocalDateTime.now();

        Routine routine = routineValidator.validateRoutineOwnership(routineId, user, now);

        List<SubRoutine> subRoutines = subRoutineRepository
            .findByRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                routineId, now, now);

        // 서브 루틴 목록 Dto로 변환
        List<SubRoutineSearchResultDto> subRoutineSearchResultDtos =
            subRoutines.stream()
            .map(subRoutine -> routineMapper.toSubRoutineSearchResultDto(subRoutine, null))
            .toList();

        // 루틴 관련 정보 Dto로 변환
        return routineMapper.toRoutineSearchResultDto(routine, subRoutineSearchResultDtos, null);
    }

    // 루틴의 완료 여부를 갱신하는 메서드입니다.
    @Transactional
    public void updateRoutineCompletionStatus(User user, UpdateRoutineCompletionRequest request) {
        List<RoutineCompletionInfo> routineCompletionInfos = request.getRoutineCompletionInfos();

        for (RoutineCompletionInfo routineCompletionInfo : routineCompletionInfos) {

            routineValidator.validateRoutineOwnership(user, routineCompletionInfo);

            // 기존 완료 여부 엔티티가 존재하는지 조회
            RoutineCompletion existingRoutineCompletion = routineCompletionRepository
                .findByRoutineIdAndPerformedDateAndRoutineHistorySeqAndRoutineType(
                    routineCompletionInfo.getRoutineId(),
                    request.getPerformedDate(),
                    routineCompletionInfo.getHistorySeq(),
                    routineCompletionInfo.getRoutineType());

            // 이미 엔티티가 존재하는 경우 완료여부 갱신
            if (existingRoutineCompletion != null) {
                existingRoutineCompletion.updateCompleteYn(routineCompletionInfo.getCompleteYn());
            }
            else { // 유저가 한번도 체크하지 않아서 RoutineCompletion 엔티티가 생기지 않은 경우 엔티티 생성
                RoutineCompletion newRoutineCompletion =
                    routineFactory.createRoutineCompletion(request, routineCompletionInfo);

                routineCompletionRepository.save(newRoutineCompletion);
            }
        }
    }

    // v2에서 사용하는 루틴 삭제 메서드
    private void deleteV2Routine(User user, Long v2RoutineId, LocalDate today) {
        RoutineV2 routineV2 = routineV2Repository.findByUserAndRoutineId(user, v2RoutineId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        RoutineInfoV2 routineInfoV2 = routineV2.getRoutineInfo();

        // 오늘 이후 루틴 내역 모두 삭제 (Hard Delete)
        List<RoutineV2> routinesV2AfterToday = routineV2Repository
            .findByRoutineInfoAndRoutineDateAfter(routineInfoV2, today);

        List<Long> routineIds = routinesV2AfterToday.stream()
            .map(RoutineV2::getRoutineId)
            .toList();

        routineV2Repository.deleteAllPhysicallyByIds(routineIds); // 물리 삭제

        routineInfoV2.updateRoutineEndDate(today); // 종료 일자를 삭제 당일로 변경
        routineInfoV2.updateRoutineDeletedYn(true); // 루틴 삭제 여부 갱신
    }

    // v1에서 사용하는 루틴 삭제 메서드
    private void deleteV1Routine(User user, UUID v1RoutineId, LocalDateTime now) {
        Routine routine = routineValidator.validateRoutineOwnership(v1RoutineId, user, now);

        // 기존 루틴, 서브 루틴의 이력 종료일시 및 deleteAt 갱신
        routine.updateHistoryEndDateTime(now);
        routine.setDeleteAt(now);

        // 서브 루틴을 순회하면서 이력 종료일시 및 deleteAt 갱신
        subRoutineRepository.findByRoutineId(v1RoutineId)
            .forEach(subRoutine -> {
                subRoutine.updateHistoryEndDateTime(now);
                subRoutine.setDeleteAt(now);
            });
    }

    // routineCompletionId에 해당하는 완료 여부 데이터 삭제
    private void deleteRoutineCompletionIfRoutineIdMatches(Long routineCompletionId, UUID routineId) {

        // 완료 여부가 생성되지 않은 루틴일 경우
        if (routineCompletionId == null) {
            return;
        }

        RoutineCompletion routineCompletion = routineCompletionRepository.findById(routineCompletionId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE_COMPLETION));

        if (!routineCompletion.getRoutineId().equals(routineId)) {
            throw new CustomException(ErrorCode.ROUTINE_ID_MISMATCH);
        }

        routineCompletionRepository.delete(routineCompletion);
    }


    /**
     * 특정 기간(startDate ~ endDate)의 루틴을 조회하는 메서드
     * 루틴과 변경 루틴에 대한 조회를 통해 조회시 변경분에 대한 부분을 반영한다.
     */
    private RoutineSearchResponse queryRoutines(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 루틴 테이블의 살아있는 이력을 모두 조회한다.
        // todo: 추후 루틴 시작일시와 종료일시가 추가되면 조회 기간안에 루틴 종료일시 혹은 시작일시가 존재하는지를 파악하여 해당 기간내에 존재하는 루틴만 조회하도록 수정이 필요하다.
        List<Routine> routines = routineRepository
            .findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                user.getUserId(), now, now);

        // 2. 조회기간의 각 요일별로 일치하는 루틴, 서브루틴을 조회해 날짜별 루틴으로 그룹핑하여 DTO로 변환
        Map<LocalDate, List<RoutineSearchResultDto>> routinesByDateResponse =
            buildRoutinesGroupedByDate(startDate, endDate, routines, now);

        // 3. 변경 루틴 테이블의 변경된 루틴 날짜가 startDate ~ endDate인 이력을 모두 조회한다.
        List<ChangedRoutine> changedRoutines = changedRoutineRepository
            .findByUserIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqualAndChangedRoutineDateBetween(
                user.getUserId(), now, now, startDate, endDate);

        // 4. 3번 과정에서 가져온 루틴에서 날짜별로 변경된 루틴을 적용하여 루틴을 제거하거나 추가
        applyChangedRoutines(changedRoutines, routinesByDateResponse, now);

        // 루틴(대분류)는 실행 시간순으로 정렬한다. 만약 실행시간이 동일하면 어떻게 정렬할까?
        for(LocalDate key: routinesByDateResponse.keySet()) {
            routinesByDateResponse.get(key).sort((a, b)
                    -> a.getExecutionTime().compareTo(b.getExecutionTime()));
        }

        return RoutineSearchResponse.builder()
                .routines(routinesByDateResponse)
                .build();
    }

    // 조회한 루틴에서 날짜별로 변경된 루틴을 적용하여 루틴을 제거하거나 추가
    private void applyChangedRoutines(List<ChangedRoutine> changedRoutines,
        Map<LocalDate, List<RoutineSearchResultDto>> routinesByDateResponse, LocalDateTime now) {

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
                List<ChangedSubRoutine> changedSubRoutines = changedSubRoutineRepository
                    .findByChangedRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                        changedRoutine.getChangedRoutinePk().getId(), now, now);

                // 변경 서브루틴 List DTO 생성
                List<SubRoutineSearchResultDto> changedSubRoutineSearchResultList = new ArrayList<>();
                for (ChangedSubRoutine changedSubRoutine : changedSubRoutines) {

                    // 변경 서브 루틴완료 여부를 파악
                    RoutineCompletion changedSubRoutineCompletion =
                        routineCompletionRepository.findByRoutineIdAndPerformedDateAndRoutineHistorySeqAndRoutineType(
                            changedSubRoutine.getChangedSubRoutinePk().getId(),
                            changedRoutineDate,
                            changedSubRoutine.getChangedSubRoutinePk().getHistorySeq(),
                            RoutineType.CHANGED_SUB_ROUTINE);

                    SubRoutineSearchResultDto changedSubRoutineSearchResultDto =
                        routineMapper.toChangedSubRoutineSearchResultDto(changedSubRoutine, changedSubRoutineCompletion);
                    changedSubRoutineSearchResultList.add(changedSubRoutineSearchResultDto);
                }

                // 변경 서브루틴 정렬
                changedSubRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                // 변경루틴 완료여부 조회
                RoutineCompletion changedRoutineCompletion =
                    routineCompletionRepository.findByRoutineIdAndPerformedDateAndRoutineHistorySeqAndRoutineType(
                        changedRoutine.getChangedRoutinePk().getId(),
                        changedRoutineDate,
                        changedRoutine.getChangedRoutinePk().getHistorySeq(),
                        RoutineType.CHANGED_ROUTINE);

                RoutineSearchResultDto changedRoutineSearchResultDto = routineMapper.toChangedRoutineSearchResultDto(
                    changedRoutine, changedSubRoutineSearchResultList, changedRoutineCompletion);

                routinesByDateResponse.get(changedRoutine.getChangedRoutineDate()).add(changedRoutineSearchResultDto);
            }
        }
    }

    // 조회기간의 각 요일별로 일치하는 루틴, 서브루틴을 조회해 날짜별 루틴으로 그룹핑하여 DTO로 변환
    private Map<LocalDate, List<RoutineSearchResultDto>> buildRoutinesGroupedByDate(
        LocalDate startDate, LocalDate endDate, List<Routine> routines, LocalDateTime now) {

        // 루틴을 날짜별로 묶어서 반환할 Map을 날짜별로 초기화 해놓는다.
        Map<LocalDate, List<RoutineSearchResultDto>> routinesByDateResponse = new HashMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            routinesByDateResponse.put(date, new ArrayList<>()); // 현재 날짜의 Map을 초기화
            DayOfWeek currentDayOfWeek = date.getDayOfWeek(); // 현재 날짜의 요일(ex: 2025-07-22 -> TUESDAY)

            // 조회해온 루틴을 순회하면서 현재 날짜의 요일과 루틴의 반복요일이 일치하는 경우 Map에 해당 루틴을 담는다.
            for (Routine routine : routines) {
                // 루틴의 반복요일이 현재 날짜의 요일과 일치하는지 확인
                if (routine.getRepeatDay().contains(currentDayOfWeek)) {
                    // 현재 루틴의 ID를 FK로 가지는 서브루틴 조회
                    List<SubRoutine> subRoutines = subRoutineRepository
                        .findByRoutineIdAndDeletedAtIsNullAndHistoryStartDateTimeBeforeAndHistoryEndDateTimeGreaterThanEqual(
                            routine.getRoutinePk().getId(), now, now);

                    // 서브루틴 List DTO 생성
                    List<SubRoutineSearchResultDto> subRoutineSearchResultList = new ArrayList<>();
                    for (SubRoutine subRoutine : subRoutines) {
                        // 서브 루틴 완료 여부 조회
                        RoutineCompletion subRoutineCompletion =
                            routineCompletionRepository.findByRoutineIdAndPerformedDateAndRoutineHistorySeqAndRoutineType(
                                subRoutine.getSubRoutinePk().getId(),
                                date,
                                subRoutine.getSubRoutinePk().getHistorySeq(),
                                RoutineType.SUB_ROUTINE);

                        SubRoutineSearchResultDto subRoutineSearchResultDto =
                            routineMapper.toSubRoutineSearchResultDto(subRoutine, subRoutineCompletion);

                        subRoutineSearchResultList.add(subRoutineSearchResultDto);
                    }

                    // 서브루틴을 sortOrder 순으로 정렬
                    subRoutineSearchResultList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

                    // 루틴 완료 여부 조회
                    RoutineCompletion routineCompletion =
                        routineCompletionRepository.findByRoutineIdAndPerformedDateAndRoutineHistorySeqAndRoutineType(
                            routine.getRoutinePk().getId(),
                            date,
                            routine.getRoutinePk().getHistorySeq(),
                            RoutineType.ROUTINE);

                    RoutineSearchResultDto routineSearchResultDto =
                        routineMapper.toRoutineSearchResultDto(routine, subRoutineSearchResultList, routineCompletion);

                    routinesByDateResponse.get(date).add(routineSearchResultDto); // map에 현재날짜에 해당하는 루틴을 담는다.
                }
            }
        }
        return routinesByDateResponse;
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
