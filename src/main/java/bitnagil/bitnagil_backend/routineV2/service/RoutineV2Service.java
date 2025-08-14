package bitnagil.bitnagil_backend.routineV2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.routineV2.request.UpdateRoutineCompletionInfo;
import bitnagil.bitnagil_backend.routineV2.request.UpdateRoutineCompletionRequest;
import bitnagil.bitnagil_backend.routineV2.response.RoutineV2SearchResponse;
import bitnagil.bitnagil_backend.routineV2.response.RoutineV2SearchResultDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_backend.routineInfoV2.repository.RoutineInfoV2Repository;
import bitnagil.bitnagil_backend.routineInfoV2.service.RoutineInfoV2Factory;
import bitnagil.bitnagil_backend.routineV2.domain.RoutineV2;
import bitnagil.bitnagil_backend.routineV2.repository.RoutineV2Repository;
import bitnagil.bitnagil_backend.routineV2.request.RegisterRoutineV2Request;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * [v2] 루틴 관련된 서비스 로직을 담은 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RoutineV2Service {

    private final RoutineInfoV2Repository routineInfoV2Repository;
    private final RoutineInfoV2Factory routineInfoV2Factory;
    private final RoutineV2Factory routineV2Factory;
    private final RoutineV2Repository routineV2Repository;
    private final RoutineV2Mapper routineV2Mapper;

    /**
     * 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 메서드입니다.
     */
    @Transactional(readOnly = true)
    public RoutineV2SearchResponse getRoutines(User user, LocalDate startDate, LocalDate endDate) {
        return queryRoutines(user, startDate, endDate);
    }

    /**
     * 회원이 보유한 루틴 단건 조회
     */
    @Transactional(readOnly = true)
    public RoutineV2SearchResultDto getRoutine(User user, Long routineId) {
        RoutineV2 routineV2 = routineV2Repository.findByUserAndRoutineId(user, routineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

        return routineV2Mapper.toRoutineV2SearchResultDto(routineV2);
    }

    /**
     * 루틴 정보를 등록하면서 루틴 시작, 종료일자를 기반으로 루틴 내역을 생성
     */
    @Transactional
    public void registerRoutineV2(User user, RegisterRoutineV2Request request) {

        LocalDate today = LocalDate.now();

        // repeatDay가 비어 있으면 빈 리스트, 아니면 요청값 사용
        List<DayOfWeek> repeatDays = request.getRepeatDay().isEmpty() ? List.of() : request.getRepeatDay();

        // 루틴 정보 등록
        RoutineInfoV2 routineInfo = routineInfoV2Factory.createNewRoutineInfo(
            request.getRoutineName(),
            repeatDays,
            request.getExecutionTime(),
            request.getRoutineStartDate(),
            request.getRoutineEndDate(),
            request.getRecommendedRoutineType(),
            user);

        routineInfoV2Repository.save(routineInfo);

        // 루틴을 생성할 날짜 목록 생성
        List<LocalDate> targetDates = request.getRepeatDay().isEmpty()
            ? List.of(today) // 당일 루틴
            : generateRoutineDatesWithinPeriod(
                request.getRoutineStartDate(),
                request.getRoutineEndDate(),
                request.getRepeatDay());

        // 서브 루틴 완료 여부 리스트 생성
        List<Boolean> subRoutineCompleteYn = request.getSubRoutineName().stream()
            .map(completeYn -> false)
            .toList();

        // 위 날짜 목록을 바탕으로 루틴 생성
        List<RoutineV2> routinesToRegister = targetDates.stream()
            .map(routineDate -> routineV2Factory.createNewRoutine(
                routineDate,
                false,
                request.getSubRoutineName(),
                subRoutineCompleteYn,
                routineInfo
            ))
            .toList();

        routineV2Repository.saveAll(routinesToRegister);
    }

    // 루틴 완료 여부를 업데이트 하는 메서드
    @Transactional
    public void updateRoutineCompletionStatus(User user, UpdateRoutineCompletionRequest request) {
        for (UpdateRoutineCompletionInfo info : request.getRoutineCompletionInfos()) {
            RoutineV2 routineV2 = routineV2Repository.findByUserAndRoutineId(user, info.getRoutineId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROUTINE));

            // 루틴, 서브루틴 완료 여부 갱신
            routineV2.updateRoutineCompleteYn(info.getRoutineCompleteYn(), info.getSubRoutineCompleteYn());
        }
    }

    /**
     * 날짜 범위에서 주어진 요일(repeatDays)에 해당하는 날짜만 반환
     */
    private List<LocalDate> generateRoutineDatesWithinPeriod(
        LocalDate startDate, LocalDate endDate, List<DayOfWeek> repeatDays) {

        List<LocalDate> routineDatesToRegister = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (repeatDays.contains(date.getDayOfWeek())) {
                routineDatesToRegister.add(date);
            }
        }
        return routineDatesToRegister;
    }

    // 특정 기간 보유 루틴 조회
    private RoutineV2SearchResponse queryRoutines(User user, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, RoutineV2SearchResponse.RoutineData> response = new HashMap<>();

        List<RoutineV2> routineList = routineV2Repository.findByUserAndDateRange(user, startDate, endDate);

        for (RoutineV2 routineV2 : routineList) {
            LocalDate date = routineV2.getRoutineDate();
            RoutineV2SearchResultDto routineSearchResultDto = routineV2Mapper.toRoutineV2SearchResultDto(routineV2);

            // 날짜별 RoutineData 생성 혹은 가져오기
            response.computeIfAbsent(date, key -> RoutineV2SearchResponse.RoutineData.builder()
                    .routineList(new ArrayList<>())
                    .allCompleted(true) // 초기값 true
                    .build());

            RoutineV2SearchResponse.RoutineData routineData = response.get(date);

            // 리스트에 추가
            routineData.getRoutineList().add(routineSearchResultDto);

            // 하나라도 완료 안 된 루틴이 있으면 false로 변경
            if (!routineSearchResultDto.getRoutineCompleteYn()) {
                routineData.setAllCompleted(false);
            }
        }

        // 정렬 처리
        response.values().forEach(data ->
                data.getRoutineList().sort(Comparator.comparing(RoutineV2SearchResultDto::getExecutionTime))
        );

        return routineV2Mapper.toRoutineV2SearchResponse(response);
    }
}
