package inc.visor.voom.app.shared.dto;

import java.util.List;

public class OsrmResponse {
    public List<Route> routes;

    public static class Route {
        public Geometry geometry;
    }

    public static class Geometry {
        public List<List<Double>> coordinates;
    }
}
