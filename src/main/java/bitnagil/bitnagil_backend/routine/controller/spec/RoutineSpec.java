package bitnagil.bitnagil_backend.routine.controller.spec;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiErrorCodeExamples;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.routine.request.UpdateRoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.ROUTINE)
public interface RoutineSpec {

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
}
