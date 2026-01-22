package inc.visor.voom.app.shared.service;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

import inc.visor.voom.app.user.home.model.RoutePoint;

public class MapRendererService {

    private final MapView mapView;
    private Polyline routeLine;

    public MapRendererService(MapView mapView) {
        this.mapView = mapView;
    }

    public void renderMarkers(List<RoutePoint> points, Drawable icon) {

        mapView.getOverlays().removeIf(o -> o instanceof Marker);

        for (RoutePoint p : points) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(p.lat, p.lng));
            marker.setIcon(icon);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
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
}

