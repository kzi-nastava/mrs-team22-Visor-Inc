package inc.visor.voom_service.shared.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminReportResponseDto {

    private ReportResponseDto drivers;
    private ReportResponseDto users;

}
