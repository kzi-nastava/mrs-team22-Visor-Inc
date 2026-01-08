package inc.visor.voom_service.ride.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.model.RideEstimationResult;
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

        double distanceKm = calculateTotalDistance(dto);
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

    private double calculateTotalDistance(
        RideRequestCreateDTO dto
    ) {
        List<RideRequestCreateDTO.RoutePointDTO> points =
            dto.route.points.stream()
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

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
