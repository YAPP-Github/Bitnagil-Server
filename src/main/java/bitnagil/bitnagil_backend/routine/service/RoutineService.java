package bitnagil.bitnagil_backend.routine.service;

import java.time.LocalDate;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * 루틴에 관련된 서비스 로직을 담은 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RoutineService {
    public static final LocalDate END_DATE = LocalDate.of(2099, 12, 31);

    private final RoutineRepository routineRepository;
    private final SubRoutineRepository subRoutineRepository;

    // 루틴, 세부루틴을 함께 저장하는 루틴 등록 메서드
    @Transactional
    public void registerRoutine(User user, RoutineRequest routineRequest) {
        Routine routine = saveRoutine(user, routineRequest);
        saveSubRoutine(routineRequest, routine);
    }

    private Routine saveRoutine(User user, RoutineRequest routineRequest) {
        Routine routine = Routine.builder()
            .name(routineRequest.getRoutineName())
            .repeatDay(routineRequest.getDaysOfWeek())
            .executionTime(routineRequest.getExecutionTime())
            .startDate(LocalDate.now())
            .endDate(END_DATE)
            .user(user)
            .build();

        return routineRepository.save(routine);
    }

    private void saveSubRoutine(RoutineRequest routineRequest, Routine routine) {
        for (String subRoutineName : routineRequest.getSubRoutineName()) {
            SubRoutine subRoutine = SubRoutine.builder()
                .name(subRoutineName)
                .startDate(LocalDate.now())
                .endDate(END_DATE)
                .routine(routine)
                .build();

            subRoutineRepository.save(subRoutine);
        }
    }
}
