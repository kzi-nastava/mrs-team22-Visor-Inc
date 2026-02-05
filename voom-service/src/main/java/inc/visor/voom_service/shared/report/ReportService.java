package inc.visor.voom_service.shared.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.shared.report.dto.ReportDailyStatsDto;
import inc.visor.voom_service.shared.report.dto.ReportResponseDto;

@Service
public class ReportService {

    private final RideService rideService;

    public ReportService(
            RideService rideService
    ) {
        this.rideService = rideService;
    }

    public ReportResponseDto getUserReport(Long userId, LocalDateTime from, LocalDateTime to) {

        List<Ride> rides
                = rideService.getFinishedRidesByUserIdAndTimeRange(userId, from, to);

        Map<LocalDate, List<Ride>> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        ride -> ride.getFinishedAt().toLocalDate()
                ));

        List<ReportDailyStatsDto> dailyStats = ridesByDate.entrySet()
                .stream()
                .map(entry -> {

                    LocalDate date = entry.getKey();
                    List<Ride> dailyRides = entry.getValue();

                    long rideCount = dailyRides.size();

                    double totalKm = dailyRides.stream()
                            .mapToDouble(ride -> ride.getRideRequest().getRideRoute().getTotalDistanceKm())
                            .sum();

                    double totalMoney = dailyRides.stream()
                            .mapToDouble(ride -> ride.getRideRequest().getCalculatedPrice())
                            .sum();

                    return new ReportDailyStatsDto(
                            date,
                            rideCount,
                            totalKm,
                            totalMoney
                    );
                })
                .sorted(Comparator.comparing(ReportDailyStatsDto::getDate))
                .toList();

        return buildResponse(dailyStats);
    }

    public ReportResponseDto getDriverReport(Long driverId, LocalDateTime from, LocalDateTime to) {

        List<Ride> rides
                = rideService.getFinishedRidesByDriverIdAndTimeRange(driverId, from, to);

        Map<LocalDate, List<Ride>> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        ride -> ride.getFinishedAt().toLocalDate()
                ));

        List<ReportDailyStatsDto> dailyStats = ridesByDate.entrySet()
                .stream()
                .map(entry -> {

                    LocalDate date = entry.getKey();
                    List<Ride> dailyRides = entry.getValue();

                    long rideCount = dailyRides.size();

                    double totalKm = dailyRides.stream()
                            .mapToDouble(ride
                                    -> ride.getRideRequest()
                                    .getRideRoute()
                                    .getTotalDistanceKm()
                            )
                            .sum();

                    double totalMoney = dailyRides.stream()
                            .mapToDouble(ride
                                    -> ride.getRideRequest()
                                    .getCalculatedPrice()
                            )
                            .sum();

                    return new ReportDailyStatsDto(
                            date,
                            rideCount,
                            totalKm,
                            totalMoney
                    );
                })
                .sorted(Comparator.comparing(ReportDailyStatsDto::getDate))
                .toList();

        return buildResponse(dailyStats);
    }

    public ReportResponseDto getSystemReport(
        LocalDateTime from,
        LocalDateTime to
        ) {

        List<Ride> rides =
                rideService.getFinishedRidesInTimeRange(from, to);

        Map<LocalDate, List<Ride>> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(
                        ride -> ride.getFinishedAt().toLocalDate()
                ));

        List<ReportDailyStatsDto> dailyStats =
                ridesByDate.entrySet()
                        .stream()
                        .map(entry -> {

                                LocalDate date = entry.getKey();
                                List<Ride> dailyRides = entry.getValue();

                                long rideCount = dailyRides.size();

                                double totalKm = dailyRides.stream()
                                        .mapToDouble(ride ->
                                                ride.getRideRequest()
                                                        .getRideRoute()
                                                        .getTotalDistanceKm()
                                        )
                                        .sum();

                                double totalMoney = dailyRides.stream()
                                        .mapToDouble(ride ->
                                                ride.getRideRequest()
                                                        .getCalculatedPrice()
                                        )
                                        .sum();

                                return new ReportDailyStatsDto(
                                        date,
                                        rideCount,
                                        totalKm,
                                        totalMoney
                                );
                        })
                        .sorted(Comparator.comparing(ReportDailyStatsDto::getDate))
                        .toList();

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

        double avgMoneyPerDay
                = dailyStats.isEmpty()
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
