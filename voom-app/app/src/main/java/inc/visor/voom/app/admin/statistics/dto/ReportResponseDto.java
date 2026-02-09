package inc.visor.voom.app.admin.statistics.dto;

import java.util.List;

public class ReportResponseDto {

    public List<DailyStatsDto> dailyStats;
    public double totalMoney;
    public double totalKm;
    public int totalRides;

    public static class DailyStatsDto {
        public String date;
        public int rideCount;
        public double totalKm;
        public double totalMoney;
    }
}
