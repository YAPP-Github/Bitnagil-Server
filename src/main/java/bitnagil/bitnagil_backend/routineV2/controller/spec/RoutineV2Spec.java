package bitnagil.bitnagil_backend.routineV2.controller.spec;

import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.routineV2.request.RegisterRoutineV2Request;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = ApiTags.ROUTINEV2)
public interface RoutineV2Spec {

    @Operation(summary = "루틴 정보 등록 및 루틴 시작, 종료일자 사이에서 반복요일에 해당하는 날짜로 루틴 데이터를 생성합니다.")
    CustomResponseDto<Object> registerRoutine(User user, RegisterRoutineV2Request request);
}
