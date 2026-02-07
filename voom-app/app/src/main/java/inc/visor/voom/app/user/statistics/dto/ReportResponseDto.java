package inc.visor.voom.app.user.statistics.dto;

import java.util.List;

public class ReportResponseDto {

    private List<ReportDailyStatsDto> dailyStats;
    private double totalMoney;
    private double totalKm;
    private int totalRides;
    private double averageMoneyPerDay;

    public List<ReportDailyStatsDto> getDailyStats() { return dailyStats; }
    public double getTotalMoney() { return totalMoney; }
    public double getTotalKm() { return totalKm; }
    public int getTotalRides() { return totalRides; }
    public double getAverageMoneyPerDay() { return averageMoneyPerDay; }
}

