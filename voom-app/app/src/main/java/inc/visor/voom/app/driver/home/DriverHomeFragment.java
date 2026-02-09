package inc.visor.voom.app.driver.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.users.api.UserApi;
import inc.visor.voom.app.admin.users.dto.BlockNoteDto;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.arrival.ArrivalDialogFragment;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.DriverAssignedDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.driver.dto.DriverVehicleResponse;
import inc.visor.voom.app.driver.dto.StartRideDto;
import inc.visor.voom.app.driver.finish.FinishRideDialogFragment;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.DriverActivityApi;
import inc.visor.voom.app.shared.api.NotificationApi;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.NotificationDto;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;
import inc.visor.voom.app.shared.dto.driver.DriverStateChangeDto;
import inc.visor.voom.app.shared.dto.ride.LatLng;
import inc.visor.voom.app.shared.dto.ride.RideCancellationDto;
import inc.visor.voom.app.shared.dto.ride.RideResponseDto;
import inc.visor.voom.app.shared.dto.ride.RideStopDto;
import inc.visor.voom.app.shared.helper.ConvertHelper;
import inc.visor.voom.app.shared.helper.DistanceHelper;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.service.NotificationService;
import inc.visor.voom.app.shared.service.NotificationWsService;
import inc.visor.voom.app.user.tracking.dto.RidePanicDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeFragment extends Fragment {

    private MapView map;
    private MapRendererService mapRenderer;
    private DriverHomeViewModel viewModel;
    private RouteRepository routeRepository;
    private boolean hasFocused = false;

    private GeoPoint pickupPoint = null;
    private GeoPoint dropoffPoint = null;
    private boolean arrivalDialogShown = false;
    private boolean finishDialogShown = false;
    private DriverAssignedDto currentAssignment = null;
    private Long currentRideId = null;
    private List<RoutePointDto> currentRoute = null;
    private NotificationWsService notificationWsService;
    private SwitchMaterial switchMaterial;
    private DriverActivityApi driverActivityApi;
    private CompositeDisposable compositeDisposable;
    private Button stopButton;
    private Button panicButton;
    private RideApi rideApi;
    private GeoPoint lastFocusPoint;

    private boolean isSuspended = false;
    private String blockReason = null;



    public DriverHomeFragment() {
        super(R.layout.fragment_driver_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastFocusPoint = null;

        rideApi = RetrofitClient.getInstance().create(RideApi.class);

        compositeDisposable = new CompositeDisposable();
        viewModel = new ViewModelProvider(this).get(DriverHomeViewModel.class);
        switchMaterial = view.findViewById(R.id.toggle_status);

        stopButton = view.findViewById(R.id.stop_ride);
        panicButton = view.findViewById(R.id.panic_ride);

        Log.d("SWITCH_DEBUG", "Switch found: " + (switchMaterial != null));
        if (switchMaterial != null) {
            Log.d("SWITCH_DEBUG", "Switch enabled: " + switchMaterial.isEnabled());
            Log.d("SWITCH_DEBUG", "Switch clickable: " + switchMaterial.isClickable());
        }

        Disposable disposable = DataStoreManager.getInstance()
            .getUserId()
            .subscribe(userId -> {

                Log.d("NOTIF", "Connecting WS for user: " + userId);

                notificationWsService =
                        new NotificationWsService(requireContext(), userId);

                notificationWsService.connect();

            }, throwable -> {
                Log.e("NOTIF", "Failed to get userId", throwable);
            }
        );

        compositeDisposable.add(disposable);

        driverActivityApi = RetrofitClient.getInstance().create(DriverActivityApi.class);

        handleToggleStatus();
        disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
            driverActivityApi.getDriverState(userId).enqueue(new Callback<DriverStateChangeDto>() {
                @Override
                public void onResponse(@NonNull Call<DriverStateChangeDto> call, @NonNull Response<DriverStateChangeDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String currentState = response.body().getCurrentState();
                        final boolean isActive = "ACTIVE".equals(currentState);

                        switchMaterial.setOnCheckedChangeListener(null);
                        switchMaterial.setChecked(isActive);
                        handleToggleStatus();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<DriverStateChangeDto> call, @NonNull Throwable t) {
                }
            });
        });

        compositeDisposable.add(disposable);

        NotificationApi api = RetrofitClient.getInstance().create(NotificationApi.class);

        api.getUnread().enqueue(new Callback<List<NotificationDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationDto>> call, @NonNull Response<List<NotificationDto>> response) {

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
            public void onFailure(@NonNull Call<List<NotificationDto>> call, @NonNull Throwable t) {
                Log.e("EXCEPTION", "Exception: ", t);
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

        Disposable disposableBlock = DataStoreManager.getInstance()
                .getUserId()
                .subscribe(userId -> {

                    UserApi userApi = RetrofitClient.getInstance()
                            .create(UserApi.class);

                    userApi.getActiveBlock(userId)
                            .enqueue(new Callback<BlockNoteDto>() {
                                @Override
                                public void onResponse(@NonNull Call<BlockNoteDto> call,
                                                       @NonNull Response<BlockNoteDto> response) {

                                    Log.d("BLOCK_CHECK", "HTTP CODE: " + response.code());
                                    Log.d("BLOCK_CHECK", "RAW BODY: " + response.body());

                                    if (!response.isSuccessful()) {
                                        Log.d("BLOCK_CHECK", "Request not successful");
                                        return;
                                    }

                                    if (response.body() == null) {
                                        Log.d("BLOCK_CHECK", "Body is NULL");
                                        return;
                                    }

                                    Log.d("BLOCK_CHECK", "Active: " + response.body().active);
                                    Log.d("BLOCK_CHECK", "Reason: " + response.body().reason);

                                    if (response.body().active) {
                                        isSuspended = true;
                                        blockReason = response.body().reason;

                                        Log.d("BLOCK_CHECK", "USER IS SUSPENDED → SHOWING UI");

                                        showSuspendedState(view);
                                    } else {
                                        Log.d("BLOCK_CHECK", "User is NOT suspended");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<BlockNoteDto> call,
                                                      @NonNull Throwable t) {
                                    Log.e("BLOCK_CHECK", "FAILED REQUEST", t);
                                }
                            });

                });

        compositeDisposable.add(disposableBlock);


        routeRepository = new RouteRepository();

        observeAssignedRide();
        setupChart(view);
        setupMap(view);
        loadOngoingRide();
        loadDriversAndStartSimulation();
        observeDrivers();
        handleRideStop();
        handleRidePanic();
    }

    private void setupChart(View view) {
        PieChart pieChart = view.findViewById(R.id.pie_chart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Completed"));
        entries.add(new PieEntry(25f, "Pending"));
        entries.add(new PieEntry(35f, "Cancelled"));

        PieDataSet dataSet = new PieDataSet(entries, "Ride Statistics");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Rides");
        pieChart.animateY(1000);
        pieChart.invalidate();


        DriverApi driverApi = RetrofitClient
                .getInstance()
                .create(DriverApi.class);

        driverApi.getMyVehicle().enqueue(new Callback<DriverVehicleResponse>() {
            @Override
            public void onResponse(@NonNull Call<DriverVehicleResponse> call, @NonNull Response<DriverVehicleResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                Long myId = response.body().driverId;
                viewModel.setMyDriverId(myId);
                tryOpenArrivalDialog();
                tryOpenFinishDialog();
            }

            @Override
            public void onFailure(@NonNull Call<DriverVehicleResponse> call, @NonNull Throwable t) {
                Log.e("EXCEPTION", "Exception: ", t);
                Toast.makeText(requireContext(), "Something went wrong",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showSuspendedState(View view) {

        TextView tvReason = view.findViewById(R.id.tv_block_reason);
        if (tvReason != null && blockReason != null) {
            tvReason.setText("Reason: " + blockReason);
        }

        View content = view.findViewById(R.id.layout_content);
        View suspendedLayout = view.findViewById(R.id.layout_suspended);

        if (content != null) {
            content.setVisibility(View.GONE);
        }

        if (suspendedLayout != null) {
            suspendedLayout.setVisibility(View.VISIBLE);
        }
    }



    private void observeAssignedRide() {

        viewModel.getAssignedRide()
            .observe(getViewLifecycleOwner(), dto -> {
                if (dto == null) return;
                drawRideMarkers(dto);
                currentAssignment = dto;
                arrivalDialogShown = false;

                currentRideId = dto.rideId;
                currentRoute = dto.route;

                RoutePointDto pickup = dto.route.stream()
                        .findFirst()
                        .orElse(null);

                RoutePointDto dropoff = dto.route.stream()
                        .filter(r -> r.type == RoutePointType.DROPOFF)
                        .findFirst()
                        .orElse(null);


                Log.d("ARRIVAL_DEBUG", "Route: " + pickup);
                if (pickup != null) {
                    pickupPoint = new GeoPoint(pickup.lat, pickup.lng);
                }

                if (dropoff != null) {
                    dropoffPoint = new GeoPoint(dropoff.lat, dropoff.lng);
                }

            }
        );
    }

    private void openArrivalDialog() {

        if (currentRoute == null || currentRideId == null) return;

        String pickupAddress = currentRoute.stream()
                .filter(p -> RoutePointType.PICKUP == p.type)
                .findFirst()
                .map(p -> p.address)
                .orElse("Unknown location");

        ArrivalDialogFragment dialog = ArrivalDialogFragment.newInstance(pickupAddress);

        dialog.setOnAcceptRideListener(() -> {
            startRide(currentRideId, currentRoute);
        });

        dialog.setOnCancelRideListener(this::cancelRide);

        dialog.show(getParentFragmentManager(), "arrival_dialog");
    }

    private void cancelRide(String reason) {
        Disposable disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
            RideCancellationDto dto = new RideCancellationDto();
            dto.setUserId(userId);
            dto.setMessage(reason);

            if (this.currentRideId == null) {
                showStatusSnackbar(getView(), "There is not current ride available", false);
                return;
            }

            rideApi.cancel(this.currentRideId, dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        showStatusSnackbar(getView(), "Cancelled ride!", false);
                    } else {
                        showStatusSnackbar(getView(), "Failed to cancel ride!", false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    showStatusSnackbar(getView(), "Network error: Check connection and try again " + t, false);
                }
            });
        });
        compositeDisposable.add(disposable);
    }

    private void openFinishDialog() {

        if (currentRoute == null || currentRideId == null) {
            return;
        }

        String destinationAddress = currentRoute.stream()
                .filter(p -> p.type == RoutePointType.DROPOFF)
                .findFirst()
                .map(p -> p.address)
                .orElse("Unknown location");

        FinishRideDialogFragment dialog = FinishRideDialogFragment.newInstance(destinationAddress);
        dialog.setListener(this::finishRide);

        dialog.show(getParentFragmentManager(), "finish_dialog");

    }

    private void finishRide() {
        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        rideApi.finishOngoing().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "Ride finished",
                            Toast.LENGTH_LONG).show();
                    finishDialogShown = true;
                    mapRenderer.clearRoute();
                    mapRenderer.renderMarkers(List.of(), requireContext().getDrawable(R.drawable.ic_location_24));
                } else {
                    Toast.makeText(requireContext(), "Something went wrong",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e("EXCEPTION", "Exception: ", t);
            }
        });
    }

    private void startRide(Long rideId, List<RoutePointDto> routePoints) {

        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        StartRideDto payload = new StartRideDto();
        payload.routePoints = routePoints;

        rideApi.startRide(rideId, payload)
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(requireContext(),"Ride started", Toast.LENGTH_LONG).show();

                        arrivalDialogShown = true;

                    } else {
                        Toast.makeText(requireContext(),
                                "Failed to start ride",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    Log.e("EXCEPTION", "Exception: ", t);
                }
            });
    }


    private void drawRideMarkers(DriverAssignedDto dto) {

        List<RoutePointDto> sorted =
                new ArrayList<>(dto.route);

        sorted.sort(
                Comparator.comparingInt(p -> p.orderIndex != null ? p.orderIndex : 0)
        );

        mapRenderer.renderMarkers(
                ConvertHelper.convertToRoutePoints(sorted),
                requireContext().getDrawable(R.drawable.ic_location_24)
        );

        routeRepository.fetchRouteFromPoints(
                ConvertHelper.convertToRoutePoints(sorted),
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
                    public void onFailure(@NonNull Call<OsrmResponse> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        Log.e("EXCEPTION", "Exception: ", t);
                    }
                }
        );
    }

    private void setupMap(View view) {
        map = view.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        IMapController controller = map.getController();
        controller.setZoom(15.0);
        controller.setCenter(new GeoPoint(45.2671, 19.8335));

        mapRenderer = new MapRendererService(map);

        map.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }
    private void loadDriversAndStartSimulation() {

        DriverApi driverApi = RetrofitClient
                .getInstance()
                .create(DriverApi.class);

        driverApi.getActiveDrivers().enqueue(new Callback<List<DriverSummaryDto>>() {

            @Override
            public void onResponse(@NonNull Call<List<DriverSummaryDto>> call,@NonNull Response<List<DriverSummaryDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                viewModel.setActiveDrivers(response.body());

                viewModel.startSimulation();
            }

            @Override
            public void onFailure(@NonNull Call<List<DriverSummaryDto>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e("EXCEPTION", "Exception: ", t);
            }
        });
    }

    private void observeDrivers() {
        viewModel.getSimulationManager()
            .getDrivers()
            .observe(getViewLifecycleOwner(), drivers -> {

                mapRenderer.renderDrivers(drivers);

                Long myId = viewModel.getMyDriverId().getValue();
                if (myId == null) return;

                for (SimulatedDriver d : drivers) {
                    if (d.id == myId) {
                        if (pickupPoint != null && !arrivalDialogShown) {

                            double distance = DistanceHelper.distanceInMeters(
                                    d.currentPosition.getLatitude(),
                                    d.currentPosition.getLongitude(),
                                    pickupPoint.getLatitude(),
                                    pickupPoint.getLongitude()
                            );

                            if (distance <= 30) {
                                arrivalDialogShown = true;
                                openArrivalDialog();
                            }
                        }

                        if (dropoffPoint != null && !finishDialogShown) {
                            double distance = DistanceHelper.distanceInMeters(
                                    d.currentPosition.getLatitude(),
                                    d.currentPosition.getLongitude(),
                                    dropoffPoint.getLatitude(),
                                    dropoffPoint.getLongitude()
                            );

                            if (distance <= 30) {
                                arrivalDialogShown = true;
                                openFinishDialog();
                            }
                        }

                        if (!hasFocused) {
                            map.getController().setZoom(18.0);
                            hasFocused = true;
                        }

                        lastFocusPoint = d.currentPosition;
                    }
                    tryOpenArrivalDialog();
                    tryOpenFinishDialog();
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    private void loadOngoingRide() {

        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        rideApi.getOngoingRide().enqueue(new Callback<ActiveRideDto>() {

            @Override
            public void onResponse(@NonNull Call<ActiveRideDto> call, @NonNull Response<ActiveRideDto> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("ONGOING_RIDE", "No active ride");
                    return;
                }

                ActiveRideDto activeRide = response.body();



                if (activeRide.routePoints == null || activeRide.routePoints.isEmpty()) {
                    return;
                }

                Log.d("ONGOING_RIDE", "Restoring ride id: " + activeRide.toString());

                restoreRideOnMap(activeRide);
            }

            @Override
            public void onFailure(@NonNull Call<ActiveRideDto> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e("EXCEPTION", "Exception: ", t);
            }
        });
    }

    private void restoreRideOnMap(ActiveRideDto activeRide) {
        currentRideId = activeRide.rideId;
        currentRoute = activeRide.routePoints;
        arrivalDialogShown = false;

        List<RoutePointDto> sorted = new ArrayList<>(activeRide.routePoints);

        mapRenderer.renderMarkers(
                ConvertHelper.convertToRoutePoints(sorted),
                requireContext().getDrawable(R.drawable.ic_location_24)
        );

        routeRepository.fetchRouteFromPoints(
                ConvertHelper.convertToRoutePoints(sorted),
                new Callback<OsrmResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<OsrmResponse> call, @NonNull Response<OsrmResponse> response) {

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
                    public void onFailure(@NonNull Call<OsrmResponse> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        Log.e("EXCEPTION", "Exception: ", t);
                    }
                }
        );

        for (RoutePointDto p : sorted) {
            if (RoutePointType.PICKUP.equals(p.type)) {
                pickupPoint = new GeoPoint(p.lat, p.lng);
                tryOpenArrivalDialog();
            }
            if (RoutePointType.DROPOFF == p.type) {
                dropoffPoint = new GeoPoint(p.lat, p.lng);
                tryOpenFinishDialog();
            }
        }
        Log.d("ONGOING_RIDE", "Ride restored successfully");
    }

    private void tryOpenArrivalDialog() {


        if (arrivalDialogShown) {
            return;
        }

        if (pickupPoint == null) {
            return;
        }

        Long myId = viewModel.getMyDriverId().getValue();

        if (myId == null) {
            return;
        }

        List<SimulatedDriver> drivers =
                viewModel.getSimulationManager()
                        .getDrivers()
                        .getValue();

        if (drivers == null) {
            return;
        }

        boolean foundMe = false;

        for (SimulatedDriver d : drivers) {

            if (d.id == myId) {

                foundMe = true;

                if (d.currentPosition == null) {
                    return;
                }

                double distance = DistanceHelper.distanceInMeters(
                        d.currentPosition.getLatitude(),
                        d.currentPosition.getLongitude(),
                        pickupPoint.getLatitude(),
                        pickupPoint.getLongitude()
                );


                if (distance <= 30) {
                    arrivalDialogShown = true;
                    openArrivalDialog();
                }
            }
        }
    }

    private void tryOpenFinishDialog() {
        if (finishDialogShown) {
            return;
        }

        if (dropoffPoint == null) {
            return;
        }

        Long myId = viewModel.getMyDriverId().getValue();

        if (myId == null) {
            return;
        }

        List<SimulatedDriver> drivers =
                viewModel.getSimulationManager()
                        .getDrivers()
                        .getValue();

        if (drivers == null) {
            return;
        }

        for (SimulatedDriver d : drivers) {

            if (d.id == myId) {

                if (d.currentPosition == null) {
                    return;
                }

                double distance = DistanceHelper.distanceInMeters(
                        d.currentPosition.getLatitude(),
                        d.currentPosition.getLongitude(),
                        dropoffPoint.getLatitude(),
                        dropoffPoint.getLongitude()
                );

                if (distance <= 30) {
                    finishDialogShown = true;
                    openFinishDialog();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (notificationWsService != null) {
            notificationWsService.disconnect();
            notificationWsService = null;
        }
        compositeDisposable.dispose();
        map = null;
    }

    private void handleToggleStatus() {
        Log.d("SWITCH_DEBUG", "handleToggleStatus called");
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Disposable disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
                final String state = isChecked ? "ACTIVE" : "INACTIVE";
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                DriverStateChangeDto dto = new DriverStateChangeDto();
                dto.setCurrentState(state);
                dto.setPerformedAt(LocalDateTime.now().format(formatter));
                dto.setUserId(userId);


                driverActivityApi.changeDriverState(dto).enqueue(new Callback<DriverStateChangeDto>() {
                    @Override
                    public void onResponse(@NonNull Call<DriverStateChangeDto> call, @NonNull Response<DriverStateChangeDto> response) {

                        if (response.isSuccessful()) {
                            String msg = isChecked ? "You are now online" : "You are now offline";
                            showStatusSnackbar(switchMaterial, msg, false);
                        } else {
                            switchMaterial.setOnCheckedChangeListener(null);
                            switchMaterial.setChecked(!isChecked);
                            switchMaterial.setOnCheckedChangeListener((buttonView2, isChecked2) -> handleToggleStatus());
                            showStatusSnackbar(switchMaterial, "Failed to update status", true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DriverStateChangeDto> call, @NonNull Throwable t) {
                        switchMaterial.setOnCheckedChangeListener(null);
                        switchMaterial.setChecked(!isChecked);
                        switchMaterial.setOnCheckedChangeListener((buttonView2, isChecked2) -> handleToggleStatus());
                        showStatusSnackbar(switchMaterial, "Network error: Status not changed", true);
                    }
                });
            });
            compositeDisposable.add(disposable);
        });
    }

    private void showStatusSnackbar(View view, String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark, null));
        }
        snackbar.show();
    }

    private void handleRideStop() {
        stopButton.setOnClickListener(view -> {
            Disposable disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
                final RideStopDto dto = new RideStopDto();
                dto.setUserId(userId);

                if (this.currentRideId == null) {
                    showStatusSnackbar(view, "There is not current ride available", true);
                    return;
                } else if (lastFocusPoint == null) {
                    showStatusSnackbar(view, "There is no focus point available", true);
                    return;
                }

                final LatLng point = new LatLng();
                point.setLat(lastFocusPoint.getLatitude());
                point.setLng(lastFocusPoint.getLongitude());
                dto.setPoint(point);

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                dto.setTimestamp(LocalDateTime.now().format(formatter));

                rideApi.stopRide(this.currentRideId, dto).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RideResponseDto> call, @NonNull Response<RideResponseDto> response) {
                        if (response.isSuccessful()) {
                            showStatusSnackbar(view, "Ride ended successfully", false);
                        } else {
                            showStatusSnackbar(view, "Could not stop ride: Server error", true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RideResponseDto> call, @NonNull Throwable t) {
                        showStatusSnackbar(view, "Network failure: Try stopping again " + t, true);
                    }
                });
            });
            compositeDisposable.add(disposable);
        });
    }

    private void handleRidePanic() {
        panicButton.setOnClickListener(view -> {
            Disposable disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
                RidePanicDto dto = new RidePanicDto();
                dto.setUserId(userId);

                if (this.currentRideId == null) {
                    showStatusSnackbar(view, "There is not current ride available", true);
                    return;
                }

                rideApi.panic(this.currentRideId, dto).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            showStatusSnackbar(view, "Panic alert sent to dispatcher!", true);
                        } else {
                            showStatusSnackbar(view, "Failed to send panic alert!", true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        showStatusSnackbar(view, "Network error: Check connection and try again " + t, true);
                    }
                });
            });
            compositeDisposable.add(disposable);
        });
    }
}
