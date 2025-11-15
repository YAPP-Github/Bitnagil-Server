package bitnagil.bitnagil_backend.report.service;

import bitnagil.bitnagil_backend.report.domain.Report;
import bitnagil.bitnagil_backend.report.repository.ReportRepository;
import bitnagil.bitnagil_backend.report.request.ReportRegisterRequest;
import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    // 제보 등록
    @Transactional
    public void registerReport(User user, ReportRegisterRequest request, List<MultipartFile> images) {
        Report report = Report.builder()
                .reportCategory(request.getReportCategory())
//                .reportImageUrls(re)
                .reportContent(request.getReportContent())
                .reportLocation(request.getReportLocation())
                .reportTitle(request.getReportTitle())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();

        reportRepository.save(report);
    }
}
