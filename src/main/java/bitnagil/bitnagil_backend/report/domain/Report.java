package bitnagil.bitnagil_backend.report.domain;

import bitnagil.bitnagil_backend.global.utils.StringListConverter;
import bitnagil.bitnagil_backend.report.domain.enums.ReportCategory;
import bitnagil.bitnagil_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

/**
 * 제보에 관련된 정보들을 관리하는 클래스입니다.
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE report SET deleted_at = NOW() WHERE report_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // 제보ID

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
}
