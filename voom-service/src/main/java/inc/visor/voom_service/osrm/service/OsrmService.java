package inc.visor.voom_service.osrm.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import inc.visor.voom_service.osrm.dto.LatLng;

@Service
public class OsrmService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<LatLng> getRoute(LatLng start, LatLng end) {

        String url
                = "https://router.project-osrm.org/route/v1/driving/"
                + start.lng() + "," + start.lat() + ";"
                + end.lng() + "," + end.lat()
                + "?overview=full&geometries=geojson";

        Map<String, Object> response
                = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> routes
                = (List<Map<String, Object>>) response.get("routes");

        Map<String, Object> geometry
                = (Map<String, Object>) routes.get(0).get("geometry");

        List<List<Double>> coordinates
                = (List<List<Double>>) geometry.get("coordinates");

        return coordinates.stream()
                .map(c -> new LatLng(c.get(1), c.get(0)))
                .toList();
    }

    public List<LatLng> getRoute(List<LatLng> points) {
        if (points.size() < 2) {
            return List.of();
        }

        String coordinates = points.stream()
                .map(p -> p.lng() + "," + p.lat())
                .reduce((a, b) -> a + ";" + b)
                .orElseThrow();

        String url
                = "https://router.project-osrm.org/route/v1/driving/"
                + coordinates
                + "?overview=full&geometries=geojson";

        Map<String, Object> response
                = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> routes
                = (List<Map<String, Object>>) response.get("routes");

        Map<String, Object> geometry
                = (Map<String, Object>) routes.get(0).get("geometry");

        List<List<Double>> coords
                = (List<List<Double>>) geometry.get("coordinates");

        return coords.stream()
                .map(c -> new LatLng(c.get(1), c.get(0)))
                .toList();
    }

}
