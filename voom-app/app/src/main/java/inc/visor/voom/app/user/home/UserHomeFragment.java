package inc.visor.voom.app.user.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Polyline;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Callback;


import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.service.OsrmService;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserHomeFragment extends Fragment {

    private MapView mapView;
    private UserHomeViewModel viewModel;

    private OsrmService osrmService;

    private Polyline routeLine;

    public UserHomeFragment() {
        super(R.layout.fragment_user_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserHomeViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        GeoPoint noviSad = new GeoPoint(45.2396, 19.8227);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(noviSad);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        osrmService = retrofit.create(OsrmService.class);

        mapView.getOverlays().add(new org.osmdroid.views.overlay.MapEventsOverlay(
                new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {

                        if (Boolean.TRUE.equals(viewModel.isRideLocked().getValue())) {
                            return false;
                        }

                        String address = getAddressFromLatLng(
                                p.getLatitude(),
                                p.getLongitude()
                        );

                        RoutePoint point = new RoutePoint(
                                p.getLatitude(),
                                p.getLongitude(),
                                address,
                                0,
                                RoutePoint.PointType.DROPOFF
                        );

                        viewModel.handleMapClick(
                                p.getLatitude(),
                                p.getLongitude(),
                                address
                        );

                        return true;
                    }
                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        return false;
                    }
                }
        ));

        requireView().findViewById(R.id.btn_clear_route)
                .setOnClickListener((v) -> {
                    viewModel.clearRoute();
                });


        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {

            renderMarkers(points);
            renderForm(points);
            renderPitstops(points);

            drawRoute(points);
        });

        viewModel.isRideLocked().observe(getViewLifecycleOwner(), locked -> {

            requireView().findViewById(R.id.btn_confirm).setEnabled(!locked);
            requireView().findViewById(R.id.dd_vehicle).setEnabled(!locked);
            requireView().findViewById(R.id.dd_time).setEnabled(!locked);
        });
    }
    private void renderMarkers(List<RoutePoint> points) {

        mapView.getOverlays().removeIf(o ->
                o instanceof org.osmdroid.views.overlay.Marker
        );

        for (RoutePoint p : points) {

            org.osmdroid.views.overlay.Marker marker =
                    new org.osmdroid.views.overlay.Marker(mapView);

            marker.setPosition(new GeoPoint(p.lat, p.lng));

            marker.setIcon(
                    requireContext().getDrawable(R.drawable.ic_location_24)
            );

            marker.setAnchor(
                    org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                    org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
            );

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }


    private void renderForm(List<RoutePoint> points) {

        var pickup = requireView().findViewById(
                R.id.et_pickup
        );
        var dropoff = requireView().findViewById(
                R.id.et_dropoff
        );

        if (points.isEmpty()) {
            ((android.widget.EditText) pickup).setText("");
            ((android.widget.EditText) dropoff).setText("");
            return;
        }

        ((android.widget.EditText) pickup)
                .setText(points.get(0).address);

        if (points.size() > 1) {
            RoutePoint last = points.get(points.size() - 1);
            ((android.widget.EditText) dropoff)
                    .setText(last.address);
        } else {
            ((android.widget.EditText) dropoff).setText("");
        }
    }

    private void renderPitstops(List<RoutePoint> points) {

        com.google.android.material.chip.ChipGroup group =
                requireView().findViewById(R.id.chip_pitstops);

        group.removeAllViews();

        for (int i = 1; i < points.size() - 1; i++) {

            int index = i;
            RoutePoint p = points.get(i);

            com.google.android.material.chip.Chip chip =
                    new com.google.android.material.chip.Chip(requireContext());

            chip.setText("Stop " + index);
            chip.setCloseIconVisible(true);

            chip.setOnCloseIconClickListener(v ->
                    viewModel.removePoint(index)
            );

            chip.setOnClickListener(v ->
                    viewModel.setAsDropoff(index)
            );

            group.addView(chip);
        }
    }

    private String getAddressFromLatLng(double lat, double lng) {
        try {
            android.location.Geocoder geocoder =
                    new android.location.Geocoder(requireContext());

            List<android.location.Address> addresses =
                    geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void drawRoute(List<RoutePoint> points) {

        if (points.size() < 2) {

            if (routeLine != null) {
                mapView.getOverlays().remove(routeLine);
                routeLine = null;
                mapView.invalidate();
            }

            return;
        }


        List<RoutePoint> sorted = new ArrayList<>(points);
        Collections.sort(sorted, Comparator.comparingInt(p -> p.orderIndex));

        StringBuilder coordsBuilder = new StringBuilder();

        for (int i = 0; i < sorted.size(); i++) {
            coordsBuilder.append(sorted.get(i).lng)
                    .append(",")
                    .append(sorted.get(i).lat);

            if (i < sorted.size() - 1) {
                coordsBuilder.append(";");
            }
        }

        osrmService.getRoute(
                coordsBuilder.toString(),
                "full",
                "geojson"
        ).enqueue(new Callback<OsrmResponse>() {

            @Override
            public void onResponse(Call<OsrmResponse> call, Response<OsrmResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<List<Double>> coords =
                        response.body().routes.get(0).geometry.coordinates;

                List<GeoPoint> geoPoints = new ArrayList<>();

                for (List<Double> c : coords) {
                    geoPoints.add(new GeoPoint(c.get(1), c.get(0)));
                }

                if (routeLine != null) {
                    mapView.getOverlays().remove(routeLine);
                }

                routeLine = new org.osmdroid.views.overlay.Polyline();
                routeLine.setPoints(geoPoints);
                routeLine.setColor(android.graphics.Color.parseColor("#2563eb"));
                routeLine.setWidth(8f);

                mapView.getOverlays().add(routeLine);
                mapView.invalidate();
            }

            @Override
            public void onFailure(Call<OsrmResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
}
