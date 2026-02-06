package inc.visor.voom.app.driver.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.api.RideApi;
import inc.visor.voom.app.driver.arrival.ArrivalDialogFragment;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.DriverAssignedDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.driver.dto.DriverVehicleResponse;
import inc.visor.voom.app.driver.dto.StartRideDto;
import inc.visor.voom.app.driver.finish.FinishRideDialogFragment;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.NotificationApi;
import inc.visor.voom.app.shared.dto.NotificationDto;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;
import inc.visor.voom.app.shared.helper.ConvertHelper;
import inc.visor.voom.app.shared.helper.DistanceHelper;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.service.NotificationService;
import inc.visor.voom.app.user.home.model.RoutePoint;
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

    public DriverHomeFragment() {
        super(R.layout.fragment_driver_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverHomeViewModel.class);

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


        routeRepository = new RouteRepository();

        observeAssignedRide();

        setupChart(view);
        setupMap(view);
        loadOngoingRide();
        loadDriversAndStartSimulation();
        observeDrivers();
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
            public void onResponse(Call<DriverVehicleResponse> call,
                                   Response<DriverVehicleResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                Long myId = response.body().driverId;
                viewModel.setMyDriverId(myId);
                tryOpenArrivalDialog();
                tryOpenFinishDialog();
            }

            @Override
            public void onFailure(Call<DriverVehicleResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

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

                });
    }


    private void openArrivalDialog() {

        if (currentRoute == null || currentRideId == null) return;

        String pickupAddress = currentRoute.stream()
                .filter(p -> RoutePointType.PICKUP == p.type)
                .findFirst()
                .map(p -> p.address)
                .orElse("Unknown location");

        ArrivalDialogFragment dialog =
                ArrivalDialogFragment.newInstance(pickupAddress);

        dialog.setListener(() -> {
            startRide(currentRideId, currentRoute);
        });

        dialog.show(getParentFragmentManager(), "arrival_dialog");
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
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "Ride finished",
                            Toast.LENGTH_LONG).show();
                    finishDialogShown = true;
                    mapRenderer.clearRoute();
                    mapRenderer.renderMarkers(List.of(), requireContext().getDrawable(R.drawable.ic_location_24));
                } else {
                    Toast.makeText(requireContext(),
                            "Something went wrong",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_LONG).show();
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
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(requireContext(),
                                    "Ride started",
                                    Toast.LENGTH_LONG).show();

                            arrivalDialogShown = true;

                        } else {
                            Toast.makeText(requireContext(),
                                    "Failed to start ride",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        t.printStackTrace();
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
                    public void onFailure(Call<OsrmResponse> call, Throwable t) {
                        t.printStackTrace();
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
            public void onResponse(Call<List<DriverSummaryDto>> call,
                                   Response<List<DriverSummaryDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                viewModel.setActiveDrivers(response.body());

                viewModel.startSimulation();
            }

            @Override
            public void onFailure(Call<List<DriverSummaryDto>> call, Throwable t) {
                t.printStackTrace();
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

                            GeoPoint point = d.currentPosition;

                            if (!hasFocused) {
                                map.getController().setZoom(18.0);
                                hasFocused = true;
                            }

                            map.getController().animateTo(point);
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
            public void onResponse(Call<ActiveRideDto> call,
                                   Response<ActiveRideDto> response) {

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
            public void onFailure(Call<ActiveRideDto> call, Throwable t) {
                Log.e("ONGOING_RIDE", "Failed to load ongoing ride", t);
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
                } else {
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
        map = null;
    }
}
