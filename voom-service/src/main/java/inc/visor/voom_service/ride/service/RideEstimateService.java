package inc.visor.voom_service.ride.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.shared.RoutePointDto;
import inc.visor.voom_service.shared.utils.GeoUtil;
import inc.visor.voom_service.vehicle.model.VehicleType;

@Service
public class RideEstimateService {

    private static final double PRICE_PER_KM = 120.0;

    public RideEstimationResult estimate(
        RideRequestCreateDTO dto,
        VehicleType vehicleType
    ) {
        validateRoute(dto);

        double distanceKm = calculateTotalDistance(dto.route.points);
        double price =
            vehicleType.getBasePrice()
            + distanceKm * PRICE_PER_KM;

        return new RideEstimationResult(
            round2(distanceKm),
            round2(price)
        );
    }


    private void validateRoute(RideRequestCreateDTO dto) {
        if (dto.route == null || dto.route.points == null) {
            throw new IllegalArgumentException("Route is required");
        }

        if (dto.route.points.size() < 2) {
            throw new IllegalArgumentException(
                "Route must contain at least pickup and dropoff"
            );
        }
    }

    public double calculateTotalDistance(
        List<RideRequestCreateDTO.RoutePointDTO> dto
    ) {
        List<RideRequestCreateDTO.RoutePointDTO> points =
            dto.stream()
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
        List<RoutePointDto> points =
            dto.stream()
                .sorted(Comparator.comparingInt(p -> p.getOrderIndex()))
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

        return round2(total);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
