export type ReportResponseDTO = {
    dailyStats: ReportDailyStatsDTO[];
    totalMoney: number;
    totalKm: number;
    totalRides: number;
    averageMoneyPerDay: number;
}

export type ReportDailyStatsDTO ={
    date: string;
    rideCount: number;
    totalKm: number;
    totalMoney: number;
}
