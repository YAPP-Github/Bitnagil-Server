package bitnagil.bitnagil_backend.routineV2.controller;

import bitnagil.bitnagil_backend.routineV2.response.RoutineV2SearchResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.routineV2.controller.spec.RoutineV2Spec;
import bitnagil.bitnagil_backend.routineV2.request.RegisterRoutineV2Request;
import bitnagil.bitnagil_backend.routineV2.service.RoutineV2Service;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/routines")
public class RoutineV2Controller implements RoutineV2Spec {

    private final RoutineV2Service routineV2Service;

    // 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 API입니다.
    @GetMapping
    public CustomResponseDto<RoutineV2SearchResponse> getRoutines(@CurrentUser User user,
                                                                  @RequestParam @NotNull LocalDate startDate,
                                                                  @RequestParam @NotNull LocalDate endDate) {
        return CustomResponseDto.from(routineV2Service.getRoutines(user, startDate, endDate));
    }

    @PostMapping("")
    public CustomResponseDto<Object> registerRoutine(@CurrentUser User user, @RequestBody RegisterRoutineV2Request request) {
        routineV2Service.registerRoutineV2(user, request);

        return CustomResponseDto.from(null);
    }
}
