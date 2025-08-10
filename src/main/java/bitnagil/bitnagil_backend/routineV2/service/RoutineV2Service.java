package bitnagil.bitnagil_backend.routineV2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

}
