package inc.visor.voom.app.shared.helper;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class ConvertHelper {

    public static List<RoutePoint> convertToRoutePoints(List<RoutePointDto> list) {

        List<RoutePoint> result = new ArrayList<>();

        if (list == null) return result;

        for (RoutePointDto p : list) {

            if (p == null) continue;

            RoutePoint.PointType type;

            switch (p.type) {
                case PICKUP:
                    type = RoutePoint.PointType.PICKUP;
                    break;

                case DROPOFF:
                    type = RoutePoint.PointType.DROPOFF;
                    break;

                default:
                    type = RoutePoint.PointType.STOP;
            }

            int safeOrderIndex = p.orderIndex != null ? p.orderIndex : 0;

            String safeAddress = p.address != null ? p.address : "";

            result.add(
                    new RoutePoint(
                            p.lat,
                            p.lng,
                            safeAddress,
                            safeOrderIndex,
                            type
                    )
            );
        }
        return result;
    }


}
