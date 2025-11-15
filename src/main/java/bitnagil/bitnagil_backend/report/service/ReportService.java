package bitnagil.bitnagil_backend.report.service;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.report.domain.Report;
import bitnagil.bitnagil_backend.report.repository.ReportRepository;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    // 제보 등록
    @Transactional
    public long registerReport(User user, ReportRegisterRequest request) {
        Report report = Report.builder()
                .reportCategory(request.getReportCategory())
                .reportContent(request.getReportContent())
                .reportLocation(request.getReportLocation())
                .reportTitle(request.getReportTitle())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();

        reportRepository.save(report);
        return report.getReportId();
    }

    @Transactional
    public void updateImages(Long reportId, List<String> urls) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT));

        report.updateReportImageUrls(urls);
    }
}
