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
        checkDuplicateRoutineName(routineRequest);

        Routine routine = saveRoutine(user, routineRequest);
        saveSubRoutine(routineRequest, routine);
    }

    private void checkDuplicateRoutineName(RoutineRequest routineRequest) {
        if (routineRepository.existsByName(routineRequest.getRoutineName())) {
            throw new CustomException(ErrorCode.ROUTINE_ALREADY_EXISTS);
        }
    }

    private Routine saveRoutine(User user, RoutineRequest routineRequest) {
        Routine routine = Routine.createRoutine(user, routineRequest, END_DATE);
        routineRepository.save(routine);
        return routine;
    }

    private void saveSubRoutine(RoutineRequest routineRequest, Routine routine) {
        for (String subRoutineName : routineRequest.getSubRoutineName()) {
            SubRoutine subRoutine = SubRoutine.createSubRoutine(subRoutineName, routine, END_DATE);
            subRoutineRepository.save(subRoutine);
        }
    }
}
