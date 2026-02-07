package inc.visor.voom.app.user.statistics.dto;

public class ReportDailyStatsDto {

    private String date;
    private int rideCount;
    private double totalKm;
    private double totalMoney;

    public String getDate() { return date; }
    public int getRideCount() { return rideCount; }
    public double getTotalKm() { return totalKm; }
    public double getTotalMoney() { return totalMoney; }
}

