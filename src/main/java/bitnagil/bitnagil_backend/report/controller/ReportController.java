package bitnagil.bitnagil_backend.report.controller;

import bitnagil.bitnagil_backend.global.annotation.CurrentUser;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.report.controller.spec.ReportSpec;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.report.service.ReportService;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/report")
public class ReportController implements ReportSpec {

    private final ReportService reportService;

    // 제보 등록 API
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponseDto<Object> registerReport(@CurrentUser User user,
                                                    @RequestPart("request") ReportRegisterRequest request,
                                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        reportService.registerReport(user, request, images);
        return CustomResponseDto.from(null);
    }
}
