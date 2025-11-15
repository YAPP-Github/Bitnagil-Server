package bitnagil.bitnagil_backend.report.controller.spec;

import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = ApiTags.REPORT)
public interface ReportSpec {

    @Operation(summary = "제보등록",
            description = "회원이 제보를 등록합니다.")
    CustomResponseDto<Object> registerReport(User user, @RequestPart ReportRegisterRequest request, @RequestPart List<MultipartFile> images);
}
