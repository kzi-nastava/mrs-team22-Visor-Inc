package inc.visor.voom_service.shared.report.dto;

import java.time.LocalDate;

public record ReportDailyStatsDto(LocalDate date, Long rideCount, Double totalKm, Double totalMoney) {

}
