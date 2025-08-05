package bitnagil.bitnagil_backend.routine.controller.spec;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.routine.request.DeleteRoutineByDayRequest;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineCompletionRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResponse;
import bitnagil.bitnagil_backend.routine.response.RoutineSearchResultDto;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

@Tag(name = ApiTags.ROUTINE)
public interface RoutineSpec {
    @Operation(summary = "루틴 조회",
            description = "회원이 특정 기간에 보유한 루틴에 대한 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "startDate", description = "조회 시작일", required = true, example = "2025-07-01"),
            @Parameter(name = "endDate", description = "조회 종료일", required = true, example = "2025-07-13")
    })
    CustomResponseDto<RoutineSearchResponse> getRoutines(User user, @NotNull LocalDate startDate, @NotNull LocalDate endDate);

    @Operation(summary = "루틴 및 서브 루틴을 등록합니다.")
    CustomResponseDto<Object> registerRoutine(User user, RegisterRoutineRequest registerRoutineRequest);

    @Operation(summary = "루틴 및 서브 루틴을 수정합니다.",
        description = "만약 서브 루틴을 삭제했다면 subRoutineName에 null값을 저장해주세요.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ROUTINE, ErrorCode.NOT_FOUND_SUB_ROUTINE,
        ErrorCode.ROUTINE_USER_NOT_MATCHED})
    CustomResponseDto<Object> updateRoutine(User user, UpdateRoutineRequest updateRoutineRequest);

    @Operation(summary = "루틴 및 서브 루틴을 모두 삭제합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ROUTINE, ErrorCode.ROUTINE_USER_NOT_MATCHED})
    CustomResponseDto<Object> deleteRoutine(User user, UUID routineId);

    @Operation(summary = "여러 루틴의 완료 여부를 갱신합니다.")
    @ApiErrorCodeExamples({
        ErrorCode.NOT_FOUND_ROUTINE, ErrorCode.ROUTINE_USER_NOT_MATCHED,
        ErrorCode.NOT_FOUND_SUB_ROUTINE, ErrorCode.SUB_ROUTINE_USER_NOT_MATCHED,
        ErrorCode.NOT_FOUND_CHANGED_ROUTINE, ErrorCode.CHANGED_ROUTINE_USER_NOT_MATCHED,
        ErrorCode.NOT_FOUND_CHANGED_SUB_ROUTINE, ErrorCode.CHANGED_SUB_ROUTINE_USER_NOT_MATCHED
    })
    CustomResponseDto<Object> updateRoutineCompletionStatus(User user,
        UpdateRoutineCompletionRequest updateRoutineCompletionRequest);

    @Operation(summary = "선택한 요일(당일)만 루틴을 삭제합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ROUTINE, ErrorCode.ROUTINE_USER_NOT_MATCHED,
        ErrorCode.NOT_FOUND_CHANGED_ROUTINE, ErrorCode.CHANGED_ROUTINE_USER_NOT_MATCHED})
    CustomResponseDto<Object> deleteRoutineByDay(User user, DeleteRoutineByDayRequest deleteRoutineByDayRequest);

    @Operation(summary = "루틴 수정 페이지에서 사용되는 루틴 단건을 조회합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ROUTINE, ErrorCode.ROUTINE_USER_NOT_MATCHED})
    CustomResponseDto<RoutineSearchResultDto> getRoutine(User user, UUID routineId);
}
