package bitnagil.bitnagil_backend.report.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.report.controller.spec.ReportSpec;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.report.response.ReportDetailInfoResponse;
import bitnagil.bitnagil_backend.report.response.ReportInfoResponse;
import bitnagil.bitnagil_backend.report.service.ReportService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 제보 목록 조회 API
    @GetMapping()
    public CustomResponseDto<ReportInfoResponse> getAllReportInfo(@CurrentUser User user) {
        return CustomResponseDto.from(reportService.getAllReportInfo(user));
    }

    // 제보 상세 조회 API
    @GetMapping("/{reportId}")
    public CustomResponseDto<ReportDetailInfoResponse> getReportDetailInfo(@CurrentUser User user,
                                                                            @PathVariable Long reportId) {
        return CustomResponseDto.from(reportService.getReportDetailInfo(user, reportId));
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
