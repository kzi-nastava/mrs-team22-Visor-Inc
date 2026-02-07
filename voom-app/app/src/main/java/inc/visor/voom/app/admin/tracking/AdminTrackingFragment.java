package inc.visor.voom.app.admin.tracking;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.tracking.adapter.DriverAdapter;
import inc.visor.voom.app.admin.tracking.adapter.PassengerAdapter;
import inc.visor.voom.app.databinding.FragmentAdminTrackingBinding;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.shared.repository.LocationRepository;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.DriverSimulationWsService;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTrackingFragment extends Fragment {
    private FragmentAdminTrackingBinding binding;
    private AdminTrackingViewModel viewModel;
    private DriverAdapter driverAdapter;
    private PassengerAdapter passengerAdapter;

    private MapRendererService mapRenderer;
    private RouteRepository routeRepository;
    private LocationRepository locationRepository;
    private DriverSimulationManager simulationManager;
    private DriverSimulationWsService wsService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminTrackingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AdminTrackingViewModel.class);
        this.routeRepository = new RouteRepository();

        setupRecyclerViews();
        setupSearch();
        observeViewModel();


        binding.mapView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        mapRenderer = new MapRendererService(binding.mapView);

        binding.mapView.getController().setZoom(14.0);
        binding.mapView.getController().setCenter(new GeoPoint(45.2396, 19.8227));

        simulationManager = viewModel.getSimulationManager();
        simulationManager.startInterpolationLoop();

        if (wsService == null) {
            wsService = new DriverSimulationWsService(
                    simulationManager,
                    viewModel,
                    null,
                    null
            );
            wsService.connect();
        }

        viewModel.fetchActiveDrivers();

        simulationManager.getDrivers().observe(getViewLifecycleOwner(), drivers -> {
            if (drivers != null && viewModel.getSelectedDriverId() == -1) {
                mapRenderer.renderDrivers(drivers);
            }
            if (viewModel.getSelectedDriverId() != -1) {
                for (SimulatedDriver driver : drivers) {
                    mapRenderer.removeDriver(driver.id);
                    if (driver.id == viewModel.getSelectedDriverId()) {
                        mapRenderer.renderDrivers(List.of(driver));
                    }
                }
                ;
            }
        });

    }

    private void setupRecyclerViews() {
        driverAdapter = new DriverAdapter(driver -> viewModel.setSelectedDriverId(driver.id));
        binding.rvDrivers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDrivers.setAdapter(driverAdapter);

        passengerAdapter = new PassengerAdapter();
        binding.rvPassengers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPassengers.setAdapter(passengerAdapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchTerm(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.filteredDrivers.observe(getViewLifecycleOwner(), drivers -> {
            driverAdapter.setDrivers(drivers);
            binding.tvNoDrivers.setVisibility(drivers.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.activeRide.observe(getViewLifecycleOwner(), ride -> {
            if (ride != null) {
                RoutePointDto pickup = ride.getRoutePoints().stream().filter(rp -> rp.type == RoutePointType.PICKUP).findFirst().orElse(null);
                RoutePointDto dropoff = ride.getRoutePoints().stream().filter(rp -> rp.type == RoutePointType.DROPOFF).findFirst().orElse(null);
                String pickupAddress = pickup != null ? pickup.address : "Unknown address";
                String dropoffAddress = dropoff != null ? dropoff.address : "Unknown adress";
                binding.tvRideDetails.setText("");
                binding.tvCurrentRideStatus.setText(String.format("Status: %s", ride.getStatus()));
                binding.tvPickupAddress.setText(String.format("Pickup address: %s", pickupAddress));
                binding.tvDropoffAddress.setText(String.format("Dropoff address: %s", dropoffAddress));
                String startedAt = ride.getStartedAt() != null ? ride.getStartedAt() : "Not yet started";
                binding.tvStartedAt.setText(String.format("Started at: %s", startedAt));
                String creatorText = ride.getCreatorName() + " (creator)";
                List<String> combined = Stream.concat(
                        Stream.of(creatorText),
                        ride.getPassengerNames().stream()
                ).collect(Collectors.toList());
                passengerAdapter.setPassengers(combined);
                binding.rvPassengers.setVisibility(View.VISIBLE);
                binding.tvCurrentRideStatus.setVisibility(View.VISIBLE);
                binding.tvPickupAddress.setVisibility(View.VISIBLE);
                binding.tvDropoffAddress.setVisibility(View.VISIBLE);
                binding.tvStartedAt.setVisibility(View.VISIBLE);
                mapRenderer.renderRouteMarkers(ride.getRoutePoints(), requireContext().getDrawable(R.drawable.ic_location_24));
                List<RoutePoint> routePoints = ride.getRoutePoints().stream().map(RoutePoint::new).collect(Collectors.toList());
                drawRoute(routePoints);
            } else {
                binding.tvRideDetails.setText("No active ride");
                binding.rvPassengers.setVisibility(View.GONE);
                binding.tvCurrentRideStatus.setVisibility(View.GONE);
                binding.tvPickupAddress.setVisibility(View.GONE);
                binding.tvDropoffAddress.setVisibility(View.GONE);
                binding.tvStartedAt.setVisibility(View.GONE);
                mapRenderer.clearRoute();
                mapRenderer.renderRouteMarkers(List.of());
            }
        });
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
}