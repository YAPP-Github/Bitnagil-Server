package bitnagil.bitnagil_backend.report.request;

import bitnagil.bitnagil_backend.report.domain.enums.ReportCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "제보 등록 요청 DTO")
public class ReportRegisterRequest {

    @Schema(description = "제보 명",
            example = "가로등 불 꺼짐",
            required = true)
    @NotNull
    private String reportTitle;

    @Schema(description = "제보 내용",
            example = "용산역인데 가로등 불이 안들어와요")
    private String reportContent;

    @Schema(description = "제보 카테고리",
            example = "TRANSPORTATION",
            required = true)
    @NotNull
    private ReportCategory reportCategory;

    @Schema(description = "제보 위치",
            example = "서울시 강남구 삼성동",
            required = true)
    @NotNull
    private String reportLocation;

    @Schema(description = "위도",
            example = "37.5642135",
            required = true)
    @NotNull
    private BigDecimal latitude;

    @Schema(description = "경도",
            example = "126.9780685",
            required = true)
    @NotNull
    private BigDecimal longitude;
}
