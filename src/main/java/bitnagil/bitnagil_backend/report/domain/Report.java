package bitnagil.bitnagil_backend.report.domain;

import bitnagil.common.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.utils.StringListConverter;
import bitnagil.bitnagil_backend.report.domain.enums.ReportCategory;
import bitnagil.bitnagil_backend.report.domain.enums.ReportStatus;
import bitnagil.bitnagil_domain.routineInfoV2.domain.RoutineInfoV2;
import bitnagil.bitnagil_domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 제보에 관련된 정보들을 관리하는 클래스입니다.
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@SQLDelete(sql = "UPDATE report SET deleted_at = NOW() WHERE report_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // 제보ID

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private ReportStatus reportStatus; // 제보 처리 상태

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40)")
    private ReportCategory reportCategory; // 제보카테고리

    @Convert(converter = StringListConverter.class)
    private List<String> reportImageUrls; // 제보 이미지 URL

    private String reportTitle; // 제보 명

    private String reportContent; // 제보 내용

    private String reportLocation; // 제보 위치

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude; // 위도

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude; // 경도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Report(ReportStatus reportStatus, ReportCategory reportCategory, List<String> reportImageUrls,
        String reportTitle, String reportContent, String reportLocation, BigDecimal latitude, BigDecimal longitude,
        User user) {
        this.reportStatus = reportStatus;
        this.reportCategory = reportCategory;
        this.reportImageUrls = reportImageUrls;
        this.reportTitle = reportTitle;
        this.reportContent = reportContent;
        this.reportLocation = reportLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public void updateReportImageUrls(List<String> urls) {
        this.reportImageUrls = urls;
    }
}
