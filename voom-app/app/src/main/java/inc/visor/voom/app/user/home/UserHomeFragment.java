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


import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class UserHomeFragment extends Fragment {

    private MapView mapView;
    private UserHomeViewModel viewModel;

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

        mapView.getOverlays().add(new org.osmdroid.views.overlay.MapEventsOverlay(
                new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {
                        if (Boolean.TRUE.equals(viewModel.isRideLocked().getValue())) {
                            return false;
                        }

                        viewModel.handleMapClick(p.getLatitude(), p.getLongitude());
                        return true;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        return false;
                    }
                }
        ));

        requireView().findViewById(R.id.btn_clear_route)
                .setOnClickListener(v -> viewModel.clearRoute());


        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {

            renderMarkers(points);
            renderForm(points);
            renderPitstops(points);

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
                .setText("Lat: " + points.get(0).lat);

        if (points.size() > 1) {
            RoutePoint last = points.get(points.size() - 1);
            ((android.widget.EditText) dropoff)
                    .setText("Lat: " + last.lat);
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
