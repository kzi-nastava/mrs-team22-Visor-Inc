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
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.api.RideApi;
import inc.visor.voom.app.driver.arrival.ArrivalDialogFragment;
import inc.visor.voom.app.driver.dto.DriverAssignedDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.driver.dto.DriverVehicleResponse;
import inc.visor.voom.app.driver.dto.StartRideDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
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
    private boolean arrivalDialogShown = false;
    private DriverAssignedDto currentAssignment = null;


    public DriverHomeFragment() {
        super(R.layout.fragment_driver_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverHomeViewModel.class);

        routeRepository = new RouteRepository();

        observeAssignedRide();

        setupChart(view);
        setupMap(view);
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

                    showAssignedNotification(dto);
                    drawRideMarkers(dto);
                    currentAssignment = dto;
                    arrivalDialogShown = false;


                    RoutePointDto pickup = dto.route.stream()
                            .findFirst()
                            .orElse(null);


                    Log.d("ARRIVAL_DEBUG", "Route: " + pickup);
                    if (pickup != null) {
                        pickupPoint = new GeoPoint(pickup.lat, pickup.lng);
                    }

                });
    }

    private void showAssignedNotification(DriverAssignedDto dto) {

        NotificationService.showRideAssignedNotification(
                requireContext(),
                dto.route.stream()
                        .filter(p -> "PICKUP".equals(p.type))
                        .findFirst()
                        .map(p -> p.address)
                        .orElse("Unknown location")
        );
    }

    private void openArrivalDialog(DriverAssignedDto dto) {

        String pickupAddress = dto.route.stream()
                .filter(p -> "PICKUP".equals(p.type))
                .findFirst()
                .map(p -> p.address)
                .orElse("Unknown location");

        ArrivalDialogFragment dialog =
                ArrivalDialogFragment.newInstance(pickupAddress);

        dialog.setListener(() -> {
            startRide(dto.rideId, dto);
        });

        dialog.show(getParentFragmentManager(), "arrival_dialog");
    }

    private void startRide(Long rideId, DriverAssignedDto assignedDto) {

        RideApi rideApi = RetrofitClient
                .getInstance()
                .create(RideApi.class);

        StartRideDto payload = new StartRideDto();
        payload.routePoints = assignedDto.route;

        rideApi.startRide(rideId, payload)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(),
                                    "Ride started",
                                    Toast.LENGTH_LONG).show();
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

        sorted.sort((a,b) ->
                Integer.compare(a.orderIndex, b.orderIndex));

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
                                    openArrivalDialog(currentAssignment);
                                }
                            }

                            GeoPoint point = d.currentPosition;

                            if (!hasFocused) {
                                map.getController().setZoom(18.0);
                                hasFocused = true;
                            }

                            map.getController().animateTo(point);
                        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        map = null;
    }
}
