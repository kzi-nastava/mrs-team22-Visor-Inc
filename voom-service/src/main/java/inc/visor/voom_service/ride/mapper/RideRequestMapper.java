package inc.visor.voom_service.ride.mapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.vehicle.model.VehicleType;

public class RideRequestMapper {

    private RideRequestMapper() {}

    public static RideRequest toEntity(
        RideRequestCreateDTO dto,
        User creator,
        VehicleType vehicleType,
        double calculatedPrice,
        double totalDistanceKm
    ) {
        RideRequest request = new RideRequest();

        request.setCreator(creator);
        request.setVehicleType(vehicleType);
        request.setStatus(RideRequestStatus.PENDING);
        request.setScheduleType(ScheduleType.valueOf(dto.schedule.type));
        request.setCalculatedPrice(calculatedPrice);
        request.setBabyTransport(dto.preferences.baby);
        request.setPetTransport(dto.preferences.pets);
        request.setLinkedPassengerEmails(dto.linkedPassengers);

        if (request.getScheduleType() == ScheduleType.LATER) {
            request.setScheduledTime(
                LocalDateTime.ofInstant(dto.schedule.startAt, ZoneOffset.UTC)
            );
        }

        RideRoute route = mapRoute(dto.route, totalDistanceKm);
        request.setRideRoute(route);

        return request;
    }

    private static RideRoute mapRoute(
        RideRequestCreateDTO.RouteDTO routeDto,
        double totalDistanceKm
    ) {
        RideRoute route = new RideRoute();
        route.setTotalDistanceKm(totalDistanceKm);

        List<RoutePoint> points =
            routeDto.points.stream()
                .sorted(Comparator.comparingInt(p -> p.order))
                .map(RideRequestMapper::mapRoutePoint)
                .toList();

        route.setRoutePoints(points);
        return route;
    }

    private static RoutePoint mapRoutePoint(
        RideRequestCreateDTO.RoutePointDTO dto
    ) {
        RoutePoint point = new RoutePoint();
        point.setLatitude(dto.lat);
        point.setLongitude(dto.lng);
        point.setOrderIndex(dto.order);
        point.setAddress(dto.address);
        point.setPointType(RoutePointType.valueOf(dto.type));
        return point;
    }
}
