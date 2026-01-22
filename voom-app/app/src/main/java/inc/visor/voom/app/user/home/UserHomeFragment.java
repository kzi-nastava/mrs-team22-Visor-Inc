package inc.visor.voom.app.user.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.repository.LocationRepository;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeFragment extends Fragment {

    private MapView mapView;
    private UserHomeViewModel viewModel;
    private MapRendererService mapRenderer;
    private RouteRepository routeRepository;
    private LocationRepository locationRepository;

    public UserHomeFragment() {
        super(R.layout.fragment_user_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserHomeViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(45.2396, 19.8227));

        mapRenderer = new MapRendererService(mapView);
        routeRepository = new RouteRepository();
        locationRepository = new LocationRepository(requireContext());

        setupMapClickListener();
        setupClearButton();
        observeViewModel();

        AutoCompleteTextView ddVehicle = requireView().findViewById(R.id.dd_vehicle);

        String[] vehicleLabels = {"Standard", "Luxury", "Van"};
        Integer[] vehicleValues = {1, 3, 2};

        ddVehicle.setAdapter(new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                vehicleLabels
        ));

        ddVehicle.setOnItemClickListener((parent, v, position, id) -> {
            viewModel.setVehicle(vehicleValues[position]);
        });

        AutoCompleteTextView ddTime = requireView().findViewById(R.id.dd_time);
        View tilScheduled = requireView().findViewById(R.id.til_scheduled_time);
        View tvTimeError = requireView().findViewById(R.id.tv_time_error);
        com.google.android.material.textfield.TextInputEditText etScheduled =
                requireView().findViewById(R.id.et_scheduled_time);

        String[] timeLabels = {"Now", "Later"};
        ddTime.setAdapter(new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                timeLabels
        ));

        ddTime.setOnItemClickListener((parent, v, position, id) -> {
            viewModel.setScheduleType(position == 0
                    ? UserHomeViewModel.ScheduleType.NOW
                    : UserHomeViewModel.ScheduleType.LATER
            );
        });

        etScheduled.setOnClickListener(v -> {
            java.util.Calendar now = java.util.Calendar.getInstance();
            int h = now.get(java.util.Calendar.HOUR_OF_DAY);
            int m = now.get(java.util.Calendar.MINUTE);

            new android.app.TimePickerDialog(requireContext(), (picker, hourOfDay, minute) -> {
                String hhmm = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, minute);
                viewModel.setScheduledTime(hhmm);
            }, h, m, true).show();
        });

        viewModel.getSelectedScheduleType().observe(getViewLifecycleOwner(), t -> {
            boolean later = t == UserHomeViewModel.ScheduleType.LATER;
            tilScheduled.setVisibility(later ? View.VISIBLE : View.GONE);
        });

        viewModel.getScheduledTime().observe(getViewLifecycleOwner(), hhmm -> {
            etScheduled.setText(hhmm == null ? "" : hhmm);
        });

        viewModel.isScheduledTimeValid().observe(getViewLifecycleOwner(), ok -> {
            tvTimeError.setVisibility(Boolean.TRUE.equals(ok) ? View.GONE : View.VISIBLE);
        });

        com.google.android.material.textfield.TextInputEditText etEmail =
                requireView().findViewById(R.id.et_passenger_email);
        com.google.android.material.chip.ChipGroup chipPassengers =
                requireView().findViewById(R.id.chip_passengers);

        etEmail.setOnEditorActionListener((v, actionId, event) -> {
            String email = v.getText() == null ? "" : v.getText().toString();
            boolean added = viewModel.addPassengerEmail(email);
            if (added) v.setText("");
            return true;
        });

        viewModel.getPassengerEmails().observe(getViewLifecycleOwner(), emails -> {
            chipPassengers.removeAllViews();

            for (String e : emails) {
                com.google.android.material.chip.Chip chip =
                        new com.google.android.material.chip.Chip(requireContext());
                chip.setText(e);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> viewModel.removePassengerEmail(e));
                chipPassengers.addView(chip);
            }

            etEmail.setEnabled(emails.size() < 3);
        });
    }

    private void setupMapClickListener() {
        mapView.getOverlays().add(new MapEventsOverlay(
                new MapEventsReceiver() {

                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {

                        if (Boolean.TRUE.equals(viewModel.isRideLocked().getValue())) {
                            return false;
                        }

                        String address = locationRepository
                                .getAddress(p.getLatitude(), p.getLongitude());

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
    }

    private void setupClearButton() {
        requireView().findViewById(R.id.btn_clear_route)
                .setOnClickListener(v -> viewModel.clearRoute());
    }

    private void observeViewModel() {

        viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {

            renderUI(points);
            renderMap(points);
        });

        viewModel.isRideLocked().observe(getViewLifecycleOwner(), locked -> {

            requireView().findViewById(R.id.btn_confirm).setEnabled(!locked);
            requireView().findViewById(R.id.dd_vehicle).setEnabled(!locked);
            requireView().findViewById(R.id.dd_time).setEnabled(!locked);
        });
    }

    private void renderUI(List<RoutePoint> points) {
        renderForm(points);
        renderPitstops(points);
    }

    private void renderMap(List<RoutePoint> points) {

        mapRenderer.renderMarkers(
                points,
                requireContext().getDrawable(R.drawable.ic_location_24)
        );

        if (points.size() < 2) {
            mapRenderer.clearRoute();
            return;
        }

        drawRoute(points);
    }

    private void drawRoute(List<RoutePoint> points) {

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

        routeRepository.fetchRoute(
                coordsBuilder.toString(),
                new Callback<OsrmResponse>() {

                    @Override
                    public void onResponse(Call<OsrmResponse> call,
                                           Response<OsrmResponse> response) {

                        if (!response.isSuccessful()
                                || response.body() == null) return;

                        List<List<Double>> coords =
                                response.body().routes.get(0).geometry.coordinates;

                        List<GeoPoint> geoPoints = new ArrayList<>();

                        for (List<Double> c : coords) {
                            geoPoints.add(new GeoPoint(c.get(1), c.get(0)));
                        }

                        mapRenderer.renderRoute(geoPoints);
                    }

                    @Override
                    public void onFailure(Call<OsrmResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                }
        );
    }

    private void renderForm(List<RoutePoint> points) {

        var pickup = requireView().findViewById(R.id.et_pickup);
        var dropoff = requireView().findViewById(R.id.et_dropoff);

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
