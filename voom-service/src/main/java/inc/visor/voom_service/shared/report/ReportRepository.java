package inc.visor.voom_service.shared.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import inc.visor.voom_service.shared.report.dto.ReportDailyStatsDto;


@Component
public interface ReportRepository extends Repository<Object, Long> {

    @Query("""
        SELECT new inc.visor.voom_service.report.dto.DailyReportDto(
            CAST(r.finishedAt AS date),
            COUNT(r),
            COALESCE(SUM(r.rideRequest.rideRoute.totalDistanceKm), 0),
            COALESCE(SUM(r.rideRequest.calculatedPrice), 0)
        )
        FROM Ride r
        WHERE r.status = inc.visor.voom_service.ride.model.enums.RideStatus.FINISHED
        AND r.finishedAt BETWEEN :from AND :to
        AND r.rideRequest.creator.id = :userId
        GROUP BY CAST(r.finishedAt AS date)
        ORDER BY CAST(r.finishedAt AS date)
    """)
    List<ReportDailyStatsDto> getUserDailyStats(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT new inc.visor.voom_service.report.dto.DailyReportDto(
            CAST(r.finishedAt AS date),
            COUNT(r),
            COALESCE(SUM(r.rideRequest.rideRoute.totalDistanceKm), 0),
            COALESCE(SUM(r.rideRequest.calculatedPrice), 0)
        )
        FROM Ride r
        WHERE r.status = inc.visor.voom_service.ride.model.enums.RideStatus.FINISHED
        AND r.finishedAt BETWEEN :from AND :to
        AND r.driver.id = :driverId
        GROUP BY CAST(r.finishedAt AS date)
        ORDER BY CAST(r.finishedAt AS date)
    """)
    List<ReportDailyStatsDto> getDriverDailyStats(
            @Param("driverId") Long driverId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    @Query("""
        SELECT new ReportDailyStatsDto(
            CAST(r.finishedAt AS date),
            COUNT(r),
            COALESCE(SUM(r.rideRequest.rideRoute.totalDistanceKm), 0),
            COALESCE(SUM(r.rideRequest.calculatedPrice), 0)
        )
        FROM Ride r
        WHERE r.status = inc.visor.voom_service.ride.model.enums.RideStatus.FINISHED
        AND r.finishedAt BETWEEN :from AND :to
        GROUP BY CAST(r.finishedAt AS date)
        ORDER BY CAST(r.finishedAt AS date)
    """)
    List<ReportDailyStatsDto> getAdminDailyStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

