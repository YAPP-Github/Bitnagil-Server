package bitnagil.bitnagil_backend.routine.controller;

import bitnagil.bitnagil_backend.routine.request.RoutineSearchRequest;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResponse;
import org.springframework.web.bind.annotation.*;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.routine.controller.spec.RoutineSpec;
import bitnagil.bitnagil_backend.routine.request.RoutineRequest;
import bitnagil.bitnagil_backend.routine.service.RoutineService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/routines")
public class RoutineController implements RoutineSpec {

    private final RoutineService routineService;

    @PostMapping("")
    public CustomResponseDto<Object> createRoutine(@CurrentUser User user, @RequestBody RoutineRequest routineRequest) {
        routineService.registerRoutine(user, routineRequest);

        return CustomResponseDto.from(null);
    }

    /**
     * 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 API입니다.
     */
    @GetMapping
    public CustomResponseDto<RoutineSearchResponse> getRoutines(@CurrentUser User user, @RequestBody RoutineSearchRequest routineSearchRequest) {
        return CustomResponseDto.from(routineService.getRoutines(user, routineSearchRequest));
    }
}
