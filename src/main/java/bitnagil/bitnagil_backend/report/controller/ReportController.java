package bitnagil.bitnagil_backend.report.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.report.controller.spec.ReportSpec;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.report.service.ReportService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/reports")
public class ReportController implements ReportSpec {

    private final ReportService reportService;

    // 제보 등록 API
    @PostMapping()
    public CustomResponseDto<Long> registerReport(@CurrentUser User user,
                                                    @RequestBody ReportRegisterRequest request) {
        return CustomResponseDto.from(reportService.registerReport(user, request));
    }

    /* 추후에 변경을 고려해서 소스만 남겨놓음
    // 제보 파일 저장 API
    @PutMapping(value = "/{reportId}/images")
    public CustomResponseDto<Object> updateImages(@PathVariable Long reportId,
                                                  @RequestParam List<String> urls) {
        reportService.updateImages(reportId, urls);
        return CustomResponseDto.from(null);
    }
    */
}
