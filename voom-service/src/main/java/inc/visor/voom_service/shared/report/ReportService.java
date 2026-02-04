package inc.visor.voom_service.shared.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.shared.report.dto.ReportDailyStatsDto;
import inc.visor.voom_service.shared.report.dto.ReportResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportResponseDto getUserReport(Long userId, LocalDateTime from, LocalDateTime to) {

        List<ReportDailyStatsDto> dailyStats =
                reportRepository.getUserDailyStats(userId, from, to);

        return buildResponse(dailyStats);
    }

    public ReportResponseDto getDriverReport(Long driverId, LocalDateTime from, LocalDateTime to) {

        List<ReportDailyStatsDto> dailyStats =
                reportRepository.getDriverDailyStats(driverId, from, to);

        return buildResponse(dailyStats);
    }

    public ReportResponseDto getAdminReport(LocalDateTime from, LocalDateTime to) {

        List<ReportDailyStatsDto> dailyStats =
                reportRepository.getAdminDailyStats(from, to);

        return buildResponse(dailyStats);
    }

    
    private ReportResponseDto buildResponse(List<ReportDailyStatsDto> dailyStats) {

        long totalRides = dailyStats.stream()
                .mapToLong(ReportDailyStatsDto::getRideCount)
                .sum();

        double totalMoney = dailyStats.stream()
                .mapToDouble(ReportDailyStatsDto::getTotalMoney)
                .sum();

        double totalKm = dailyStats.stream()
                .mapToDouble(ReportDailyStatsDto::getTotalKm)
                .sum();

        double avgMoneyPerDay =
                dailyStats.isEmpty()
                        ? 0
                        : totalMoney / dailyStats.size();

        return new ReportResponseDto(
                dailyStats,
                totalRides,
                totalMoney,
                totalKm,
                avgMoneyPerDay
        );
    }
}
