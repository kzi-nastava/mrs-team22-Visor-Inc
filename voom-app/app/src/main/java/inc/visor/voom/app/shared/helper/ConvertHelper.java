package inc.visor.voom.app.shared.helper;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class ConvertHelper {

    public static List<RoutePoint>
    convertToRoutePoints(List<RoutePointDto> list) {

        List<inc.visor.voom.app.user.home.model.RoutePoint> result = new ArrayList<>();

        for (RoutePointDto p : list) {

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


            result.add(
                    new RoutePoint(
                            p.lat,
                            p.lng,
                            p.address,
                            p.orderIndex,
                            type
                    )
            );
        }

        return result;
    }

}
