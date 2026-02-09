package inc.visor.voom.app.admin.statistics;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.*;

import java.text.SimpleDateFormat;
import java.util.*;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.statistics.dto.ReportResponseDto;
import inc.visor.voom.app.databinding.FragmentAdminStatisticsBinding;

public class AdminStatisticsFragment extends Fragment {

    private FragmentAdminStatisticsBinding binding;
    private AdminStatisticsViewModel viewModel;

    private Date fromDate, toDate;

    public AdminStatisticsFragment() {
        super(R.layout.fragment_admin_statistics);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminStatisticsBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AdminStatisticsViewModel.class);

        setupDatePickers();
        setupSpinner();

        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.setPinchZoom(true);
        binding.lineChart.setScaleEnabled(true);
        binding.lineChart.animateX(800);


        binding.generateButton.setOnClickListener(v -> generate());

        viewModel.getReport().observe(getViewLifecycleOwner(), this::populateUi);
    }

    private void generate() {
        if (fromDate == null || toDate == null) return;

        String from = formatDate(fromDate);
        String to = formatDate(toDate);

        viewModel.loadReport(from, to, null, null);
    }

    private void populateUi(ReportResponseDto dto) {

        int days = dto.dailyStats.size();
        double avg = days > 0 ? dto.totalMoney / days : 0;

        binding.totalRides.setText(String.format(Locale.getDefault(), "%.2f", (double) dto.totalRides));
        binding.totalKm.setText(String.format(Locale.getDefault(), "%.2f km", dto.totalKm));
        binding.totalMoney.setText(String.format(Locale.getDefault(), "%.2f €", dto.totalMoney));
        binding.averageMoney.setText(String.format(Locale.getDefault(), "%.2f €", avg));

        List<Entry> rides = new ArrayList<>();
        List<Entry> km = new ArrayList<>();
        List<Entry> money = new ArrayList<>();

        for (int i = 0; i < dto.dailyStats.size(); i++) {
            rides.add(new Entry(i, dto.dailyStats.get(i).rideCount));
            km.add(new Entry(i, (float) dto.dailyStats.get(i).totalKm));
            money.add(new Entry(i, (float) dto.dailyStats.get(i).totalMoney));
        }

        LineDataSet ds1 = new LineDataSet(rides, "Rides");
        ds1.setColor(getResources().getColor(R.color.secondary));
        ds1.setCircleColor(getResources().getColor(R.color.secondary));
        ds1.setCircleRadius(4f);
        ds1.setLineWidth(2.5f);
        ds1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ds1.setDrawFilled(true);
        ds1.setFillColor(getResources().getColor(R.color.secondary));
        ds1.setFillAlpha(60);

        LineDataSet ds2 = new LineDataSet(km, "Kilometers");
        ds2.setColor(getResources().getColor(R.color.light_blue_A400));
        ds2.setCircleColor(getResources().getColor(R.color.light_blue_A400));
        ds2.setCircleRadius(4f);
        ds2.setLineWidth(2.5f);
        ds2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ds2.setDrawFilled(true);
        ds2.setFillColor(getResources().getColor(R.color.light_blue_A400));
        ds2.setFillAlpha(60);

        LineDataSet ds3 = new LineDataSet(money, "Money");
        ds3.setColor(getResources().getColor(R.color.orange));
        ds3.setCircleColor(getResources().getColor(R.color.orange));
        ds3.setCircleRadius(4f);
        ds3.setLineWidth(2.5f);
        ds3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ds3.setDrawFilled(true);
        ds3.setFillColor(getResources().getColor(R.color.orange));
        ds3.setFillAlpha(60);

        LineData lineData = new LineData(ds1, ds2, ds3);
        binding.lineChart.setData(lineData);
        binding.lineChart.setData(new LineData(ds1, ds2, ds3));
        binding.lineChart.invalidate();
    }

    private void setupSpinner() {
        List<String> options = new ArrayList<>();
        options.add("System Overview");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        options);

        binding.userSpinner.setAdapter(adapter);
    }

    private void setupDatePickers() {
        binding.fromDateInput.setOnClickListener(v -> showDatePicker(true));
        binding.toDateInput.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    cal.set(year, month, day);
                    if (isFrom) {
                        fromDate = cal.getTime();
                        binding.fromDateInput.setText(formatDate(fromDate));
                    } else {
                        toDate = cal.getTime();
                        binding.toDateInput.setText(formatDate(toDate));
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(date);
    }
}
