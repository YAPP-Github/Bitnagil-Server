package bitnagil.bitnagil_backend.routine.controller;

import java.time.LocalDate;
import java.util.UUID;

import bitnagil.bitnagil_backend.routine.request.DeleteRoutineByDayRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineCompletionRequest;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResultDto;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResponse;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{routineId}")
    public CustomResponseDto<Object> deleteRoutine(@CurrentUser User user, @PathVariable String routineId) {
        routineService.deleteRoutine(user, routineId);

        return CustomResponseDto.from(null);
    }

    /*
     * 유저가 선택한 요일(당일)만 삭제하는 API입니다.
     */
    @DeleteMapping("/day")
    public CustomResponseDto<Object> deleteRoutineByDay(@CurrentUser User user,
        @RequestBody DeleteRoutineByDayRequest deleteRoutineByDayRequest) {
        routineService.deleteRoutineByDay(user, deleteRoutineByDayRequest);

        return CustomResponseDto.from(null);
    }

    /**
     * 회원이 보유한 특정 기간(start_date, end_date)의 루틴을 조회하는 API입니다.
     */
    @GetMapping
    public CustomResponseDto<RoutineSearchResponse> getRoutines(@CurrentUser User user,
                                                                @RequestParam @NotNull LocalDate startDate,
                                                                @RequestParam @NotNull LocalDate endDate) {
        return CustomResponseDto.from(routineService.getRoutines(user, startDate, endDate));
    }

    /**
     * 루틴 완료 여부 업데이트
     * 새 엔티티를 생성할 수도, 부분 수정할 수도 있기에 PATCH를 쓰지 않고 POST를 씁니다.
     */
    @PostMapping("/completions")
    public CustomResponseDto<Object> updateRoutineCompletionStatus(@CurrentUser User user,
        @RequestBody UpdateRoutineCompletionRequest updateRoutineCompletionRequest) {
        routineService.updateRoutineCompletionStatus(user, updateRoutineCompletionRequest);

        return CustomResponseDto.from(null);
    }

    // 루틴 수정 페이지에서 사용되는 루틴 단건 조회 API
    @GetMapping("{routineId}")
    public CustomResponseDto<RoutineSearchResultDto> getRoutine(@CurrentUser User user, @PathVariable UUID routineId) {

        return CustomResponseDto.from(routineService.getRoutine(user, routineId));
    }
}
