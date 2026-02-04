package inc.visor.voom_service.shared.report.dto;

import java.time.LocalDate;

public class ReportDailyStatsDto {

    private final LocalDate date;
    private final Long rideCount;
    private final Double totalKm;
    private final Double totalMoney;

    public ReportDailyStatsDto(
            LocalDate date,
            Long rideCount,
            Double totalKm,
            Double totalMoney
    ) {
        this.date = date;
        this.rideCount = rideCount;
        this.totalKm = totalKm;
        this.totalMoney = totalMoney;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getRideCount() {
        return rideCount;
    }

    public Double getTotalKm() {
        return totalKm;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }
}
