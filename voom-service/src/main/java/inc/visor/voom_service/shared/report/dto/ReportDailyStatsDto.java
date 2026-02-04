package inc.visor.voom_service.shared.report.dto;

import java.time.LocalDate;

public class ReportDailyStatsDto {

    private LocalDate date;
    private double totalMoney;
    private double totalKm;
    private long rideCount;
    

    public ReportDailyStatsDto() {
    }

    public ReportDailyStatsDto(LocalDate date, double totalMoney, double totalKm, long rideCount) {
        this.date = date;
        this.totalMoney = totalMoney;
        this.totalKm = totalKm;
        this.rideCount = rideCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(double totalKm) {
        this.totalKm = totalKm;
    }

    public long getRideCount() {
        return rideCount;
    }

    public void setRideCount(long rideCount) {
        this.rideCount = rideCount;
    }
}
