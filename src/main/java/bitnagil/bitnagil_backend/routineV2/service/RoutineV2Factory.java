package bitnagil.bitnagil_backend.routineV2.service;

import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_backend.routineV2.domain.RoutineV2;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 루틴 관련 엔티티 생성, 초기화 책임을 담당하는 클래스입니다.
 */
@Component
public class RoutineV2Factory {

    // 신규 Routine 엔티티 생성 및 초기화
    public RoutineV2 createNewRoutine(LocalDate routineDate, Boolean routineCompleteYn, List<String> subRoutineNames,
                                      RoutineInfoV2 routineInfo) {
        return RoutineV2.builder()
                .routineDate(routineDate)
                .routineCompleteYn(routineCompleteYn)
                .subRoutineNames(subRoutineNames)
                .subRoutineCompleteYn(initSubRoutineCompleteYn(subRoutineNames.size()))
                .routineInfo(routineInfo)
                .build();
    }

    // 서브루틴 완료 여부 리스트를 초기화 및 생성
    private  List<Boolean> initSubRoutineCompleteYn(Integer subRoutineCnt) {
        return IntStream.range(0, subRoutineCnt)
            .mapToObj(i -> false)
            .collect(Collectors.toList());
    }
}
