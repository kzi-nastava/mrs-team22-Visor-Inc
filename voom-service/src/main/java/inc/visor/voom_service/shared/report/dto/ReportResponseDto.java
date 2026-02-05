package inc.visor.voom_service.shared.report.dto;

import java.util.List;

public class ReportResponseDto {

    private List<ReportDailyStatsDto> dailyStats;

    private double totalMoney;
    private double totalKm;
    private long totalRides;

    private double averageMoneyPerDay;

    public ReportResponseDto() {
    }

    public ReportResponseDto(
        List<ReportDailyStatsDto> dailyStats,
        long totalRides,
        double totalMoney,
        double totalKm,
        double averageMoneyPerDay
    ) {
        this.dailyStats = dailyStats;
        this.totalMoney = totalMoney;
        this.totalKm = totalKm;
        this.totalRides = totalRides;
        this.averageMoneyPerDay = averageMoneyPerDay;
    }

    public void setDailyStats(List<ReportDailyStatsDto> dailyStats) {
        this.dailyStats = dailyStats;
    }

    public List<ReportDailyStatsDto> getDailyStats() {
        return dailyStats;
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

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }
}
