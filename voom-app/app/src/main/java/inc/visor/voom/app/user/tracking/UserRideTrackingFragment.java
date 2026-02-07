package inc.visor.voom.app.user.tracking;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.model.SimulatedDriver;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.service.DriverSimulationWsService;
import inc.visor.voom.app.shared.service.MapRendererService;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRideTrackingFragment extends Fragment {

    private UserRideTrackingViewModel mViewModel;

    private View trackingContainer, reviewContainer, reportForm;
    private TextView tvEta, tvAddresses, tvReportPrompt, tvEmpty;
    private RatingBar ratingDriver, ratingCar;
    private EditText etReviewComment, etReportMessage;
    private Button btnSubmitReview, btnPanic, btnSubmitReport;
    private MapView mapView;

    private LinearLayout ratingForm ;
    private TextView tvReviewSuccess;

    private MapRendererService mapRenderer;

    private DriverSimulationWsService wsService;
    private DriverSimulationManager simulationManager;

    private final RouteRepository routeRepository = new RouteRepository();

    public static UserRideTrackingFragment newInstance() {
        return new UserRideTrackingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_ride_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserRideTrackingViewModel.class);

        initializeViews(view);
        setupMap();
        setupClickListeners();

        mapRenderer = new MapRendererService(mapView);

        simulationManager = mViewModel.getSimulationManager();

        wsService = new DriverSimulationWsService(
                simulationManager,
                mViewModel,
                null,
                null
        );
        wsService.setOnPositionReceivedListener(dto -> {
            if (isAdded() && getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    mViewModel.updateRideStatus(dto);
                });
            }
        });
        wsService.connect();

        Log.e("PANIC_CHECK", "btnPanic is null? " + (btnPanic == null));

        if (btnPanic == null) {
            Log.e("BTN PANIC NULL", "BTN PANIC IS NULL");
        } else {
            Log.e("BTN NOT NULL", "NIEJ NULL");
        }


        mViewModel.initDrive();
        observeViewModel();
    }

    private void initializeViews(View v) {
        trackingContainer = v.findViewById(R.id.trackingContainer);
        reviewContainer = v.findViewById(R.id.reviewContainer);
        reportForm = v.findViewById(R.id.reportForm);
        mapView = v.findViewById(R.id.mapView);

        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEta = v.findViewById(R.id.tvEta);
        tvAddresses = v.findViewById(R.id.tvAddresses);
        tvReportPrompt = v.findViewById(R.id.tvReportPrompt);

        ratingDriver = v.findViewById(R.id.ratingDriver);
        ratingCar = v.findViewById(R.id.ratingCar);
        etReviewComment = v.findViewById(R.id.etReviewComment);
        etReportMessage = v.findViewById(R.id.etReportMessage);

        btnSubmitReview = v.findViewById(R.id.btnSubmitReview);
        this.btnPanic = v.findViewById(R.id.btnPanicUser);
        Log.println(Log.ASSERT, "PORUKA", btnPanic.toString());
        btnSubmitReport = v.findViewById(R.id.btnSubmitReport);

        ratingForm = v.findViewById(R.id.ratingForm);
        tvReviewSuccess = v.findViewById(R.id.tvReviewSuccess);


    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(45.2671, 19.8335));
    }

    private void observeViewModel() {

        mViewModel.isRideFound().observe(getViewLifecycleOwner(), found -> {
            if (!found) {
                tvEmpty.setVisibility(View.VISIBLE);
                trackingContainer.setVisibility(View.GONE);
                reviewContainer.setVisibility(View.GONE);
                return;
            }
            else {
                tvEmpty.setVisibility(View.GONE);
            }
        });

        mViewModel.getRoutePoints().observe(getViewLifecycleOwner(), this::renderMap);

        mViewModel.isRideFinished().observe(getViewLifecycleOwner(), finished -> {
            if (finished) {
                trackingContainer.setVisibility(View.GONE);
                reviewContainer.setVisibility(View.VISIBLE);
                mapView.getOverlays().clear();
            }
        });

        mViewModel.getEta().observe(getViewLifecycleOwner(), minutes -> {
            tvEta.setText("Estimated time of arrival: " + minutes + " minutes");
        });

        mViewModel.getStartAddress().observe(getViewLifecycleOwner(), start -> updateAddressText());
        mViewModel.getDestinationAddress().observe(getViewLifecycleOwner(), dest -> updateAddressText());

        mViewModel.isReviewed().observe(getViewLifecycleOwner(), reviewed -> {
            if (reviewed) {
                ratingForm.setVisibility(View.GONE);

                tvReviewSuccess.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Review Submitted!", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.hasPanicked().observe(getViewLifecycleOwner(), panicked -> {
            if (panicked) {
                Navigation.findNavController(requireView()).navigate(R.id.profileFragment);
                Toast.makeText(getContext(), "Panic activated. Notifying authorities.", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.isReported().observe(getViewLifecycleOwner(), reported -> {
            if(reported) {
                tvReportPrompt.setText("Thank you for your report. We will look into it.");
                tvReportPrompt.setTextColor(getResources().getColor(R.color.black));
                tvReportPrompt.setClickable(false);
            }
        });

        simulationManager.getDrivers().observe(getViewLifecycleOwner(), drivers -> {

            for (SimulatedDriver d : drivers) {
                if (mViewModel.getDriverId() != null && d.id == mViewModel.getDriverId()) {
                    mapRenderer.renderDrivers(List.of(d));
//                    mapView.getController().animateTo(d.currentPosition);
                    break;
                }
            }
        });
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

    private void updateAddressText() {
        String start = mViewModel.getStartAddress().getValue();
        String dest = mViewModel.getDestinationAddress().getValue();
        tvAddresses.setText("📍 " + start + " - " + dest);
    }


    private void setupClickListeners() {
        tvReportPrompt.setOnClickListener(v -> {
            int visibility = reportForm.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            reportForm.setVisibility(visibility);
        });

        btnSubmitReview.setOnClickListener(v -> {
            int dRating = (int) ratingDriver.getRating();
            int cRating = (int) ratingCar.getRating();
            String comment = etReviewComment.getText().toString();
            mViewModel.submitReview(dRating, cRating, comment);
        });

        btnPanic.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Panic Activated.", Toast.LENGTH_SHORT).show();
                Log.d("PANIC", "PANIC CLICKED");
                mViewModel.panic();
            }
        );

        btnSubmitReport.setOnClickListener(v -> {
            mViewModel.reportRide(etReportMessage.getText().toString());
            reportForm.setVisibility(View.GONE);
        });

    }

    @Override
    public void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    public void onPause() { super.onPause(); mapView.onPause(); }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (wsService != null) {
            wsService.disconnect();
        }
    }
}