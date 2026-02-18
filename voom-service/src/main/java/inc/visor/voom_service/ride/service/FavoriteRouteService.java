package inc.visor.voom_service.ride.service;

import inc.visor.voom_service.ride.dto.CreateFavoriteRouteRequest;
import inc.visor.voom_service.ride.dto.FavoriteRouteDto;
import inc.visor.voom_service.ride.exception.DuplicateFavoriteRouteException;
import inc.visor.voom_service.ride.model.FavoriteRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.repository.FavoriteRouteRepository;
import inc.visor.voom_service.shared.RoutePointDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteRouteService {

    private final FavoriteRouteRepository repository;
    private final RideEstimateService rideEstimateService;

    public FavoriteRouteService(FavoriteRouteRepository repository, RideEstimateService rideEstimateService) {
        this.repository = repository;
        this.rideEstimateService = rideEstimateService;
    }

    public void create(long userId, CreateFavoriteRouteRequest request) {

        List<RoutePointDto> incomingPoints = request.getPoints().stream()
                .sorted(Comparator.comparingInt(
                        p -> p.getOrderIndex() == null ? Integer.MAX_VALUE : p.getOrderIndex()
                ))
                .toList();

        List<FavoriteRoute> existingRoutes = repository.findAllByUserId(userId);

        for (FavoriteRoute existing : existingRoutes) {

            List<RoutePoint> existingPoints = existing.getRoutePoints()
                    .stream()
                    .sorted(Comparator.comparingInt(RoutePoint::getOrderIndex))
                    .toList();

            if (sameRoute(existingPoints, incomingPoints)) {
                throw new DuplicateFavoriteRouteException();
            }
        }

        FavoriteRoute route = new FavoriteRoute();
        route.setUserId(userId);
        route.setName(request.getName());

        List<RoutePoint> points = new ArrayList<>();

        for (int i = 0; i < incomingPoints.size(); i++) {

            RoutePointDto dto = incomingPoints.get(i);

            RoutePoint rp = new RoutePoint();

            rp.setOrderIndex(
                    dto.getOrderIndex() == null ? i : dto.getOrderIndex()
            );

            rp.setLatitude(dto.getLat());
            rp.setLongitude(dto.getLng());
            rp.setAddress(dto.getAddress());
            rp.setPointType(dto.getType());

            points.add(rp);
        }

        double estimateDistance
                = rideEstimateService.calculateTotalDistanceEstimate(incomingPoints);

        route.setRoutePoints(points);
        route.setTotalDistanceKm(estimateDistance);

        repository.save(route);
    }

    public List<FavoriteRouteDto> getAllByUserId(long userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void delete(long userId, long routeId) {
        FavoriteRoute route = repository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        if (route.getUserId() != userId) {
            throw new RuntimeException("Forbidden");
        }

        repository.delete(route);
    }

    private FavoriteRouteDto toDto(FavoriteRoute route) {
        FavoriteRouteDto dto = new FavoriteRouteDto();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setTotalDistanceKm(route.getTotalDistanceKm());

        dto.setPoints(
                route.getRoutePoints()
                        .stream()
                        .map(rp -> {
                            RoutePointDto p = new RoutePointDto();
                            p.setOrderIndex(rp.getOrderIndex());
                            p.setLat(rp.getLatitude());
                            p.setLng(rp.getLongitude());
                            p.setAddress(rp.getAddress());
                            p.setType(rp.getPointType());
                            return p;
                        })
                        .toList()
        );

        return dto;
    }

    private boolean sameRoute(
            List<RoutePoint> a,
            List<RoutePointDto> b
    ) {
        if (a.size() != b.size()) {
            return false;
        }

        for (int i = 0; i < a.size(); i++) {
            RoutePoint p1 = a.get(i);
            RoutePointDto p2 = b.get(i);

            if (p1.getPointType() != p2.getType()) {
                return false;
            }

            if (!sameCoord(p1.getLatitude(), p2.getLat())) {
                return false;
            }
            if (!sameCoord(p1.getLongitude(), p2.getLng())) {
                return false;
            }
        }

        return true;
    }

    private boolean sameCoord(double a, double b) {
        return Math.abs(a - b) < 0.0001;
    }

}
