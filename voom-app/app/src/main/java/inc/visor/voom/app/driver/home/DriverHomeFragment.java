package inc.visor.voom.app.driver.home;

import android.os.Bundle;
import android.view.View;

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
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.service.MapRendererService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeFragment extends Fragment {

    private MapView map;
    private MapRendererService mapRenderer;
    private DriverHomeViewModel viewModel;

    public DriverHomeFragment() {
        super(R.layout.fragment_driver_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverHomeViewModel.class);

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
    }

    private void setupMap(View view) {
        map = view.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        IMapController controller = map.getController();
        controller.setZoom(15.0);
        controller.setCenter(new GeoPoint(45.2671, 19.8335));

        mapRenderer = new MapRendererService(map);

        // 🔥 Sprečava NestedScrollView da krade touch
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
