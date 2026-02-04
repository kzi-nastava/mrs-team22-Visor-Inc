package inc.visor.voom_service.ride.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.shared.RoutePointDto;
import inc.visor.voom_service.shared.utils.GeoUtil;
import inc.visor.voom_service.vehicle.model.VehicleType;

@Service
public class RideEstimateService {

    private static final double PRICE_PER_KM = 1.1; // example value

    public RideEstimationResult estimate(List<RideRequestCreateDto.RoutePointDto> points, VehicleType vehicleType) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Route must contain at least pickup and drop off");
        }

        double distanceKm = calculateTotalDistance(points);
        double price = vehicleType.getPrice() + distanceKm * PRICE_PER_KM;

        return new RideEstimationResult(round(distanceKm), round(price));
    }

    public double calculateTotalDistance(List<RideRequestCreateDto.RoutePointDto> dto) {
        List<RideRequestCreateDto.RoutePointDto> points
                = dto.stream()
                        .sorted(Comparator.comparingInt(p -> p.order))
                        .toList();

        double total = 0.0;

        for (int i = 0; i < points.size() - 1; i++) {
            var a = points.get(i);
            var b = points.get(i + 1);

            total += GeoUtil.distanceKm(
                    a.lat, a.lng,
                    b.lat, b.lng
            );
        }

        return total;
    }

    public double calculateTotalDistanceEstimate(
            List<RoutePointDto> dto
    ) {
        List<RoutePointDto> points
                = dto.stream()
                        .sorted(Comparator.comparing(
                                RoutePointDto::getOrderIndex,
                                Comparator.nullsLast(Integer::compareTo)
                        ))
                        .toList();

        double total = 0.0;

        for (int i = 0; i < points.size() - 1; i++) {
            var a = points.get(i);
            var b = points.get(i + 1);

            total += GeoUtil.distanceKm(
                    a.getLat(), a.getLng(),
                    b.getLat(), b.getLng()
            );
        }

        return round(total);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
