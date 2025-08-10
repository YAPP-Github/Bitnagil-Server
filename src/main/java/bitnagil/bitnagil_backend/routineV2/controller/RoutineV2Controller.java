package bitnagil.bitnagil_backend.routineV2.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.routineV2.controller.spec.RoutineV2Spec;
import bitnagil.bitnagil_backend.routineV2.request.RegisterRoutineV2Request;
import bitnagil.bitnagil_backend.routineV2.service.RoutineV2Service;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/routines")
public class RoutineV2Controller implements RoutineV2Spec {

    private final RoutineV2Service routineV2Service;

    @PostMapping("")
    public CustomResponseDto<Object> registerRoutine(@CurrentUser User user, @RequestBody RegisterRoutineV2Request request) {
        routineV2Service.registerRoutineV2(user, request);

        return CustomResponseDto.from(null);
    }
}
