package bitnagil.bitnagil_backend.routine.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.routine.controller.spec.RoutineSpec;
import bitnagil.bitnagil_backend.routine.request.RoutineRequest;
import bitnagil.bitnagil_backend.routine.service.RoutineService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/routine")
public class RoutineController implements RoutineSpec {

    private final RoutineService routineService;

    @PostMapping("")
    public CustomResponseDto<Object> createRoutine(@CurrentUser User user, @RequestBody RoutineRequest routineRequest) {
        routineService.registerRoutine(user, routineRequest);

        return CustomResponseDto.from(null);
    }
}
