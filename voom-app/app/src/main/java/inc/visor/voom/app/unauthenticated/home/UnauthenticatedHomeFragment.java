package inc.visor.voom.app.unauthenticated.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.route.RouteEstimateRequestDto;
import inc.visor.voom.app.shared.dto.route.RouteEstimateResponseDto;
import inc.visor.voom.app.shared.repository.LocationRepository;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.DriverSimulationWsService;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnauthenticatedHomeFragment extends Fragment {

    private UnauthenticatedHomeViewModel mViewModel;
    private MapView mapView;
    private MapRendererService mapRenderer;
    private RouteRepository routeRepository;
    private LocationRepository locationRepository;
    private DriverSimulationManager simulationManager;
    private DriverSimulationWsService wsService;

    RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);


    private TextView tvDuration;
    private TextView tvDistance;

    private MaterialButton signInBtn;

    public static UnauthenticatedHomeFragment newInstance() {
        return new UnauthenticatedHomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_unauthenticated, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(UnauthenticatedHomeViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        tvDuration = view.findViewById(R.id.estimationDurationTv);
        tvDistance = view.findViewById(R.id.estimationDistanceTv);

        signInBtn = view.findViewById(R.id.btnSignIn);

        signInBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_unauthenticatedHomeFragment_to_loginFragment);
        });

        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(45.2396, 19.8227));

        mapRenderer = new MapRendererService(mapView);
        routeRepository = new RouteRepository();
        locationRepository = new LocationRepository(requireContext());

        simulationManager = mViewModel.getSimulationManager();

        simulationManager.startInterpolationLoop();


        DriverApi driverApi = RetrofitClient
                .getInstance()
                .create(DriverApi.class);

        driverApi.getActiveDrivers().enqueue(new Callback<List<DriverSummaryDto>>() {

            @Override
            public void onResponse(Call<List<DriverSummaryDto>> call,
                                   Response<List<DriverSummaryDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API", "Failed to load drivers");
                    return;
                }

                List<DriverSummaryDto> drivers = response.body();

                mViewModel.setActiveDrivers(drivers);

                wsService = new DriverSimulationWsService(
                        simulationManager,
                        mViewModel,
                        null,
                        null,
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

        simulationManager.getDrivers().observe(getViewLifecycleOwner(), drivers -> {
            if (drivers != null) {
                mapRenderer.renderDrivers(drivers);
            }
        });

    }

    private void observeViewModel() {
        mViewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {
            mapRenderer.renderMarkers(points, requireContext().getDrawable(R.drawable.ic_location_24));

            if (points.size() >= 2) {
                drawRoute(points);
            } else {
                mapRenderer.clearRoute();
            }

            renderForm(points);
            renderPitstops(points);

            RouteEstimateRequestDto payload = new RouteEstimateRequestDto(points);

            rideApi.getRouteEstimate(payload).enqueue(new Callback<RouteEstimateResponseDto>() {
                @Override
                public void onResponse(Call<RouteEstimateResponseDto> call, Response<RouteEstimateResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RouteEstimateResponseDto estimate = response.body();
                        String distanceText = String.format("Estimated distance: %.1f km", estimate.getDistance());
                        tvDistance.setText(distanceText);
                        String durationText = String.format("Estimated duration: %d minutes", estimate.getDuration());
                        tvDuration.setText(durationText);
                    } else {
                        Log.e("API_ERROR", "NE VALJA NISTA NE VALJA " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<RouteEstimateResponseDto> call, Throwable t) {
                    Log.e("API_FAILURE", "zaboravio si upaliti backend" + t.getMessage());
                }
            });


        });
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
                    mViewModel.removePoint(index)
            );

            chip.setOnClickListener(v ->
                    mViewModel.setAsDropoff(index)
            );

            group.addView(chip);
        }
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

    private void setupMapClickListener() {
        mapView.getOverlays().add(new MapEventsOverlay(
                new MapEventsReceiver() {

                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {


                        String address = locationRepository
                                .getAddress(p.getLatitude(), p.getLongitude());

                        mViewModel.handleMapClick(
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
                .setOnClickListener(v -> mViewModel.clearRoute());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UnauthenticatedHomeViewModel.class);
        // TODO: Use the ViewModel
    }

}