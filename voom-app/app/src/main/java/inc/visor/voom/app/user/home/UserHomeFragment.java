package inc.visor.voom.app.user.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.GsonBuilder;

import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.users.api.UserApi;
import inc.visor.voom.app.admin.users.dto.BlockNoteDto;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.NotificationApi;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.NotificationDto;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;
import inc.visor.voom.app.shared.dto.ScheduledRideDto;
import inc.visor.voom.app.shared.dto.StartScheduledRideDto;
import inc.visor.voom.app.shared.helper.DistanceHelper;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.shared.repository.FavoriteRouteRepository;
import inc.visor.voom.app.shared.repository.LocationRepository;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.DriverSimulationWsService;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.service.NotificationService;
import inc.visor.voom.app.shared.service.NotificationWsService;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.favorite_route.FavoriteRoutesFragment;
import inc.visor.voom.app.user.home.dialog.FavoriteRouteNameDialog;
import inc.visor.voom.app.user.home.dto.CreateFavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
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
    private DriverSimulationManager simulationManager;
    private DriverSimulationWsService wsService;

    private FavoriteRouteRepository favoriteRouteRepository;
    private NotificationWsService notificationWsService;

    private boolean isSuspended = false;
    private String blockReason = null;



    private Boolean arrivalNotified = false;

    private boolean scheduledDriverSent = false;


    public UserHomeFragment() {
        super(R.layout.fragment_user_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkIfBlocked(view);

        viewModel = new ViewModelProvider(this).get(UserHomeViewModel.class);

        DataStoreManager.getInstance()
                .getUserId()
                .subscribe(userId -> {

                    Log.d("NOTIF", "Connecting WS for user: " + userId);

                    notificationWsService =
                            new NotificationWsService(requireContext(), userId);

                    notificationWsService.connect();

                }, throwable -> {
                    Log.e("NOTIF", "Failed to get userId", throwable);
                });


        NotificationApi api =
                RetrofitClient.getInstance().create(NotificationApi.class);

        api.getUnread().enqueue(new Callback<List<NotificationDto>>() {
            @Override
            public void onResponse(Call<List<NotificationDto>> call,
                                   Response<List<NotificationDto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    for (NotificationDto n : response.body()) {
                        NotificationService.showNotification(
                                getContext(),
                                n.title,
                                n.id,
                                n.message
                        );
                    }
                }
            }
            @Override
            public void onFailure(Call<List<NotificationDto>> call, Throwable t) {
                Log.e("NOTIF", "Failed to load unread", t);
            }
        });


        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(45.2396, 19.8227));

        mapRenderer = new MapRendererService(mapView);
        routeRepository = new RouteRepository();
        locationRepository = new LocationRepository(requireContext());

        simulationManager = viewModel.getSimulationManager();

        simulationManager.startInterpolationLoop();

        favoriteRouteRepository = new FavoriteRouteRepository();

        requireView().findViewById(R.id.btn_add_favorite)
                .setOnClickListener(v -> openFavoriteDialog());

        requireView().findViewById(R.id.btn_choose_favorite)
                .setOnClickListener(v -> openFavoriteRoutes());

        DriverApi driverApi = RetrofitClient
                .getInstance()
                .create(DriverApi.class);

        Bundle args = getArguments();

        if (args != null && args.containsKey("picked_route")) {

            List<RoutePointDto> dtos =
                    (List<RoutePointDto>) args.getSerializable("picked_route");

            if (dtos != null && !dtos.isEmpty()) {
                applyPickedRoute(dtos);
            }
        }

        restoreActiveRide();

        driverApi.getActiveDrivers().enqueue(new Callback<List<DriverSummaryDto>>() {

            @Override
            public void onResponse(Call<List<DriverSummaryDto>> call,
                                   Response<List<DriverSummaryDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API", "Failed to load drivers");
                    return;
                }

                List<DriverSummaryDto> drivers = response.body();

                viewModel.setActiveDrivers(drivers);

                wsService = new DriverSimulationWsService(
                        simulationManager,
                        viewModel,
                        null,
                        rides -> handleScheduledRides(rides),
                        null
                );
                wsService.connect();
            }

            @Override
            public void onFailure(Call<List<DriverSummaryDto>> call, Throwable t) {
                Log.e("API", "Error fetching drivers", t);
            }
        });

        setupMapClickListener();
        setupClearButton();
        observeViewModel();

        requireView().findViewById(R.id.btn_confirm)
                .setOnClickListener(v -> onConfirmClicked());

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
        simulationManager.getDrivers()
                .observe(getViewLifecycleOwner(), drivers -> {

                    mapRenderer.renderDrivers(drivers);

                    if (Boolean.TRUE.equals(viewModel.isRideLocked().getValue())
                            && !arrivalNotified) {

                        List<RoutePoint> points =
                                viewModel.getRoutePoints().getValue();

                        if (points == null || points.isEmpty()) return;

                        RoutePoint pickup = points.get(0);

                        for (SimulatedDriver driver : drivers) {

                            float distance = DistanceHelper.distanceInMeters(
                                    driver.currentPosition.getLatitude(),
                                    driver.currentPosition.getLongitude(),
                                    pickup.lat,
                                    pickup.lng
                            );

                            if (distance < 15) {
                                arrivalNotified = true;
                                break;
                            }

                        }
                    }
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
    private void openFavoriteRoutes() {
        androidx.navigation.Navigation
                .findNavController(requireView())
                .navigate(R.id.favoriteRoutesFragment);
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

            View root = requireView().findViewById(R.id.root_container);
            setEnabledRecursive(root, !locked);

            if (locked) {
                mapView.setClickable(false);
                mapView.setFocusable(false);
                arrivalNotified = false;
            } else {
                mapView.setClickable(true);
                mapView.setFocusable(true);
            }
        });

    }
    private void setEnabledRecursive(View view, boolean enabled) {

        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setEnabledRecursive(vg.getChildAt(i), enabled);
            }
        }
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

        routeRepository.fetchRouteFromPoints(
                points,
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

    private void checkIfBlocked(View view) {

        DataStoreManager.getInstance()
                .getUserId()
                .subscribe(userId -> {

                    UserApi userApi = RetrofitClient.getInstance()
                            .create(UserApi.class);

                    userApi.getActiveBlock(userId)
                            .enqueue(new Callback<BlockNoteDto>() {

                                @Override
                                public void onResponse(@NonNull Call<BlockNoteDto> call,
                                                       @NonNull Response<BlockNoteDto> response) {

                                    if (!response.isSuccessful()
                                            || response.body() == null) {
                                        return;
                                    }

                                    if (response.body().active) {

                                        isSuspended = true;
                                        blockReason = response.body().reason;

                                        showSuspendedState(view);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<BlockNoteDto> call,
                                                      @NonNull Throwable t) {
                                }
                            });
                });
    }

    private void showSuspendedState(View view) {

        View content = view.findViewById(R.id.root_container);
        View suspendedLayout = view.findViewById(R.id.layout_suspended);
        TextView tvReason = view.findViewById(R.id.tv_block_reason);

        if (tvReason != null && blockReason != null) {
            tvReason.setText("Reason: " + blockReason);
        }

        if (suspendedLayout != null) {
            suspendedLayout.setVisibility(View.VISIBLE);
        }

        // Opcionalno: disable klikova
        if (content != null) {
            setEnabledRecursive(content, false);
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

    private void onConfirmClicked() {

        List<RoutePoint> points = viewModel.getRoutePoints().getValue();

        boolean pets = ((CheckBox)
                requireView().findViewById(R.id.cb_pets)).isChecked();

        boolean baby = ((CheckBox)
                requireView().findViewById(R.id.cb_baby)).isChecked();

        RideRequestDto payload =
                viewModel.buildRideRequest(points, pets, baby);

        if (payload == null) {
            Log.d("RIDE", "Invalid ride");
            return;
        }

        payload.freeDriversSnapshot =
                simulationManager.getFreeDriversSnapshot();

        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        rideApi.createRideRequest(payload)
                .enqueue(new Callback<RideRequestResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<RideRequestResponseDto> call,
                            Response<RideRequestResponseDto> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Log.e("RIDE", "Request failed");
                            return;
                        }

                        RideRequestResponseDto res = response.body();

                        if ("ACCEPTED".equals(res.status)
                                && res.driver != null) {

                            android.widget.Toast.makeText(
                                    requireContext(),
                                    "Ride accepted. Driver: "
                                            + res.driver.firstName
                                            + " "
                                            + res.driver.lastName,
                                    android.widget.Toast.LENGTH_LONG
                            ).show();

                            if (viewModel.getSelectedScheduleType().getValue()
                                    == UserHomeViewModel.ScheduleType.NOW) {

                                viewModel.lockRide();
                            }

                        } else {
                            android.widget.Toast.makeText(
                                    requireContext(),
                                    "No drivers available",
                                    android.widget.Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RideRequestResponseDto> call,
                            Throwable t
                    ) {
                        Log.e("RIDE", "Network error", t);
                    }
                });
    }

    private void restoreActiveRide() {

        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        rideApi.getOngoingRide().enqueue(new Callback<ActiveRideDto>() {

            @Override
            public void onResponse(Call<ActiveRideDto> call,
                                   Response<ActiveRideDto> response) {

                if (!response.isSuccessful()) {
                    Log.d("RESTORE", "No ongoing ride");
                    return;
                }

                if (response.body() == null) {
                    Log.d("RESTORE", "Empty body");
                    return;
                }

                ActiveRideDto ride = response.body();

                if (ride.routePoints == null || ride.routePoints.isEmpty()) return;

                Log.d("RESTORE", "Restoring ride id: " + ride.rideId);

                List<RoutePoint> points = new ArrayList<>();

                for (RoutePointDto p : ride.routePoints) {

                    RoutePoint rp = new RoutePoint(
                            p.lat,
                            p.lng,
                            p.address,
                            p.orderIndex,
                            RoutePointDto.toPointType(p.type)
                    );

                    points.add(rp);
                }

                viewModel.restoreRide(points, true);

                arrivalNotified = false;

            }

            @Override
            public void onFailure(Call<ActiveRideDto> call, Throwable t) {
                Log.e("RESTORE", "Failed to restore ride", t);
            }
        });
    }

    private void openFavoriteDialog() {

        List<RoutePoint> points = viewModel.getRoutePoints().getValue();
        if (points == null || points.size() < 2) {
            android.widget.Toast.makeText(
                    requireContext(),
                    "Pickup and dropoff required",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            return;
        }

        FavoriteRouteNameDialog dialog = new FavoriteRouteNameDialog();

        dialog.setListener(name -> {

            CreateFavoriteRouteDto dto =
                    viewModel.buildFavoriteRoute(name);

            if (dto == null) return;

            favoriteRouteRepository.createFavoriteRoute(
                    dto,
                    new Callback<Void>() {

                        @Override
                        public void onResponse(
                                Call<Void> call,
                                Response<Void> response
                        ) {

                            if (response.isSuccessful()) {
                                android.widget.Toast.makeText(
                                        requireContext(),
                                        "Route saved",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                            } else if (response.code() == 409) {
                                android.widget.Toast.makeText(
                                        requireContext(),
                                        "Route already exists",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<Void> call,
                                Throwable t
                        ) {
                            android.widget.Toast.makeText(
                                    requireContext(),
                                    "Failed to save route",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        });

        dialog.show(getParentFragmentManager(), "FavoriteDialog");
    }

    private void applyPickedRoute(List<RoutePointDto> dtos) {

        List<RoutePoint> points = new ArrayList<>();

        for (RoutePointDto p : dtos) {

            RoutePoint rp = new RoutePoint(
                    p.lat,
                    p.lng,
                    p.address,
                    p.orderIndex,
                    RoutePointDto.toPointType(p.type)
            );

            points.add(rp);
        }

        viewModel.clearRoute();
        viewModel.restoreRide(points, false);
    }

    private void handleScheduledRides(ScheduledRideDto[] rides) {
        if (rides == null || rides.length == 0) return;

        long now = System.currentTimeMillis();
        long TEN_MIN = 10 * 60 * 1000;

        ScheduledRideDto selected = null;

        for (ScheduledRideDto r : rides) {


            LocalDateTime ldt = LocalDateTime.parse(r.scheduledStartTime);

            long startMs = ldt
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long diff = startMs - now;

            if (diff >= 0 && diff <= TEN_MIN) {
                selected = r;
                break;
            }
        }

        if (selected == null) return;

        ScheduledRideDto finalSelected = selected;

        requireActivity().runOnUiThread(() -> {

            List<RoutePoint> points = new ArrayList<>();

            for (RoutePointDto p : finalSelected.route) {

                RoutePoint rp = new RoutePoint(
                        p.lat,
                        p.lng,
                        p.address,
                        p.orderIndex,
                        RoutePointDto.toPointType(p.type)
                );

                points.add(rp);
            }

            viewModel.restoreRide(points, true);
            arrivalNotified = false;

            if (!scheduledDriverSent && finalSelected.driverId != null) {

                RoutePointDto pickup = null;

                for (RoutePointDto p : finalSelected.route) {
                    if (p.type == RoutePointType.PICKUP) {
                        pickup = p;
                        break;
                    }
                }

                if (pickup != null) {

                    RideApi rideApi = RetrofitClient
                            .getInstance()
                            .create(RideApi.class);

                    StartScheduledRideDto payload = new StartScheduledRideDto();
                    payload.setDriverId(finalSelected.driverId);
                    payload.setLat(pickup.lat);
                    payload.setLng(pickup.lng);

                    rideApi.startScheduleRide(finalSelected.rideId, payload)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Log.d("SCHEDULE", "Ride started on backend");
                                    scheduledDriverSent = true; 
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("SCHEDULE", "Failed to start ride", t);
                                }
                            });
                }
            }

            android.widget.Toast.makeText(
                    requireContext(),
                    "Scheduled ride starting soon",
                    android.widget.Toast.LENGTH_LONG
            ).show();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        simulationManager.startInterpolationLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (notificationWsService != null) {
            notificationWsService.disconnect();
            notificationWsService = null;
        }

        if (wsService != null) {
            wsService.disconnect();
            wsService = null;
        }

        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
    }


}
