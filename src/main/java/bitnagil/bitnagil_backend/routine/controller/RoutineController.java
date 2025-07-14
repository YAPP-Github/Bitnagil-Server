package bitnagil.bitnagil_backend.routine.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.routine.controller.spec.RoutineSpec;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.routine.service.RoutineService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/routines")
public class RoutineController implements RoutineSpec {

    private final RoutineService routineService;

    @PostMapping("")
    public CustomResponseDto<Object> registerRoutine(@CurrentUser User user,
        @RequestBody RegisterRoutineRequest registerRoutineRequest) {
        routineService.registerRoutine(user, registerRoutineRequest);

        return CustomResponseDto.from(null);
    }

    @PatchMapping("")
    public CustomResponseDto<Object> updateRoutine(@CurrentUser User user,
        @RequestBody UpdateRoutineRequest updateRoutineRequest) {
        routineService.updateRoutine(user, updateRoutineRequest);

        return CustomResponseDto.from(null);
    }
}
