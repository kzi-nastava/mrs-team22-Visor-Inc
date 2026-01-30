package inc.visor.voom.app.shared.service;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.component.DriverInfoWindow;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class MapRendererService {

    private final MapView mapView;
    private Polyline routeLine;
    private final List<Marker> routeMarkers = new ArrayList<>();
    private final Map<Long, Marker> driverMarkers = new HashMap<>();


    public MapRendererService(MapView mapView) {
        this.mapView = mapView;
    }
    public void renderMarkers(List<RoutePoint> points, Drawable icon) {

        for (Marker m : routeMarkers) {
            mapView.getOverlays().remove(m);
        }
        routeMarkers.clear();

        for (RoutePoint p : points) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(p.lat, p.lng));
            marker.setIcon(icon);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            mapView.getOverlays().add(marker);
            routeMarkers.add(marker);
        }

        mapView.invalidate();
    }


    public void renderRoute(List<GeoPoint> geoPoints) {

        clearRoute();

        routeLine = new Polyline();
        routeLine.setPoints(geoPoints);
        routeLine.setColor(Color.parseColor("#2563eb"));
        routeLine.setWidth(8f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
    }

    public void clearRoute() {
        if (routeLine != null) {
            mapView.getOverlays().remove(routeLine);
            routeLine = null;
            mapView.invalidate();
        }
    }
    
    public void renderDrivers(List<SimulatedDriver> drivers) {

        for (SimulatedDriver driver : drivers) {

            if (driver.currentPosition == null) continue;

            Marker marker = driverMarkers.get(driver.id);

            if (marker == null) {

                marker = new Marker(mapView);
                marker.setIcon(
                        mapView.getContext()
                                .getDrawable(R.drawable.ic_driver)
                );
                marker.setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_CENTER
                );
                marker.setRelatedObject(driver);
                marker.setInfoWindow(new DriverInfoWindow(mapView));

                marker.setOnMarkerClickListener((m, mapView) -> {

                    if (m.isInfoWindowShown()) {
                        m.closeInfoWindow();
                    } else {
                        m.showInfoWindow();
                    }

                    return true;
                });

                mapView.getOverlays().add(marker);
                driverMarkers.put(driver.id, marker);
            }

            marker.setPosition(driver.currentPosition);
        }

        mapView.invalidate();
    }
    public void renderRouteMarkers(List<RoutePointDto> points) {

        for (Marker m : routeMarkers) {
            mapView.getOverlays().remove(m);
        }
        routeMarkers.clear();

        for (RoutePointDto point : points) {

            GeoPoint geoPoint = new GeoPoint(
                    point.lat,
                    point.lng
            );

            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            mapView.getOverlays().add(marker);
            routeMarkers.add(marker);
        }

        mapView.invalidate();
    }


    public void renderSimpleRoute(List<RoutePoint> points) {

        clearRoute();

        List<GeoPoint> geoPoints = new ArrayList<>();

        for (RoutePoint p : points) {
            geoPoints.add(new GeoPoint(p.lat, p.lng));
        }

        routeLine = new Polyline();
        routeLine.setPoints(geoPoints);
        routeLine.setColor(Color.parseColor("#2563eb"));
        routeLine.setWidth(6f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
    }

}

