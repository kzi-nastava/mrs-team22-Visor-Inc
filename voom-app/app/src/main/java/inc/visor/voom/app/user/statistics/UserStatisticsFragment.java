package inc.visor.voom.app.user.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import inc.visor.voom.app.databinding.FragmentUserStatisticsBinding;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.user.statistics.dto.ReportDailyStatsDto;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserStatisticsFragment extends Fragment {

    private FragmentUserStatisticsBinding binding;
    private UserStatisticsViewModel viewModel;

    // state
    private Date fromDate;
    private Date toDate;
    private boolean isSuspended = false;
    private boolean loading = false;

    private DataStoreManager storeManager;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Long currentUserId = null;

    private final SimpleDateFormat apiFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat uiFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private final DecimalFormat kmFmt = new DecimalFormat("#0.00");
    private final DecimalFormat moneyFmt = new DecimalFormat("#0.00");

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentUserStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserStatisticsViewModel.class);

        apiFmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        storeManager = DataStoreManager.getInstance(requireContext());

        disposables.add(
                storeManager.getUserId()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> {
                            currentUserId = id;
                        })
        );


        setupDatePickers();
        setupChartsDefaults();
        setupObservers();
        updateUiState();
    }

    private void setupDatePickers() {

        binding.fromInput.setOnClickListener(v -> openSingleDatePicker(true));
        binding.fromLayout.setEndIconOnClickListener(v -> openSingleDatePicker(true));

        binding.toInput.setOnClickListener(v -> openSingleDatePicker(false));
        binding.toLayout.setEndIconOnClickListener(v -> openSingleDatePicker(false));

        binding.btnGenerate.setOnClickListener(v -> onGenerateClicked());
    }

    private void openSingleDatePicker(boolean isFrom) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isFrom ? "Select From date" : "Select To date")
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Date d = new Date(selection);
            if (isFrom) {
                fromDate = stripTime(d);
                binding.fromInput.setText(uiFmt.format(fromDate));
            } else {
                toDate = stripTime(d);
                binding.toInput.setText(uiFmt.format(toDate));
            }

            validateRangeAndShowError();
            updateUiState();
        });

        picker.show(getParentFragmentManager(), isFrom ? "fromPicker" : "toPicker");
    }

    private void onGenerateClicked() {
        if (!canGenerate()) return;

        loading = true;
        updateUiState();

        Integer userId = currentUserId != null ? currentUserId.intValue() : null;

        String from = apiFmt.format(fromDate);
        String to = apiFmt.format(toDate);

        viewModel.loadReport(from, to, userId);
    }

    private void setupObservers() {
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loading = Boolean.TRUE.equals(isLoading);
            updateUiState();
        });

        viewModel.getReport().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) {
                resetUi();
                return;
            }
            populateUi(dto);
        });
    }

    private void populateUi(ReportResponseDto dto) {

        binding.txtTotalRides.setText(String.valueOf(dto.getTotalRides()));
        binding.txtTotalKm.setText(kmFmt.format(dto.getTotalKm()) + " km");
        binding.txtTotalMoney.setText(moneyFmt.format(dto.getTotalMoney()));
        binding.txtAverageMoney.setText(moneyFmt.format(dto.getAverageMoneyPerDay()));

        binding.txtTotalMoneyLabel.setText("Total expenses");
        binding.txtMoneyChartTitle.setText("Expenses per day");

        List<ReportDailyStatsDto> stats = dto.getDailyStats() != null ? dto.getDailyStats() : new ArrayList<>();
        setupRidesChart(binding.chartRides, stats);
        setupKmChart(binding.chartKm, stats);
        setupMoneyChart(binding.chartMoney, stats, "Expenses per day");
    }

    private void resetUi() {
        binding.txtTotalRides.setText("0");
        binding.txtTotalKm.setText("0.00 km");
        binding.txtTotalMoney.setText("0.00");
        binding.txtAverageMoney.setText("0.00");

        clearChart(binding.chartRides);
        clearChart(binding.chartKm);
        clearChart(binding.chartMoney);
    }

    private void setupChartsDefaults() {
        initLineChart(binding.chartRides);
        initBarChart(binding.chartKm);
        initLineChart(binding.chartMoney);
    }

    private void initLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.setNoDataText("No data");
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
        chart.getAxisLeft().setGranularity(1f);
    }

    private void initBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.setNoDataText("No data");
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
    }

    private void setupRidesChart(LineChart chart, List<ReportDailyStatsDto> stats) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            entries.add(new Entry(i, stats.get(i).getRideCount()));
            labels.add(formatChartDate(stats.get(i).getDate()));
        }

        LineDataSet ds = new LineDataSet(entries, "Rides per day");
        ds.setLineWidth(2f);
        ds.setCircleRadius(3.5f);

        chart.setData(new LineData(ds));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    private void setupKmChart(BarChart chart, List<ReportDailyStatsDto> stats) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            entries.add(new BarEntry(i, (float) stats.get(i).getTotalKm()));
            labels.add(formatChartDate(stats.get(i).getDate()));

        }

        BarDataSet ds = new BarDataSet(entries, "Kilometers per day");
        BarData data = new BarData(ds);
        data.setBarWidth(0.9f);

        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setFitBars(true);
        chart.invalidate();
    }

    private void setupMoneyChart(LineChart chart, List<ReportDailyStatsDto> stats, String label) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            entries.add(new Entry(i, (float) stats.get(i).getTotalMoney()));
            labels.add(formatChartDate(stats.get(i).getDate()));
        }

        LineDataSet ds = new LineDataSet(entries, label);
        ds.setLineWidth(2f);
        ds.setCircleRadius(3.5f);

        chart.setData(new LineData(ds));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    private void clearChart(LineChart chart) {
        chart.clear();
        chart.invalidate();
    }

    private void clearChart(BarChart chart) {
        chart.clear();
        chart.invalidate();
    }

    private void validateRangeAndShowError() {
        boolean invalid = isInvalidRange();
        binding.txtInvalidRange.setVisibility(invalid ? View.VISIBLE : View.GONE);

        // optional: oboji outline u crveno kad je invalid
        if (invalid) {
            binding.fromLayout.setError(" ");
            binding.toLayout.setError(" ");
        } else {
            binding.fromLayout.setError(null);
            binding.toLayout.setError(null);
        }
    }

    private boolean isInvalidRange() {
        return fromDate != null && toDate != null && fromDate.after(toDate);
    }

    private boolean canGenerate() {
        return fromDate != null
                && toDate != null
                && !isInvalidRange()
                && !isSuspended
                && !loading;
    }

    private void updateUiState() {
        validateRangeAndShowError();

        binding.btnGenerate.setEnabled(canGenerate());
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (isSuspended) {
            binding.btnGenerate.setEnabled(false);
            binding.btnGenerate.setText("Suspended");
        } else {
            binding.btnGenerate.setText("Generate Report");
        }
    }

    private String formatChartDate(String isoDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat output = new SimpleDateFormat("dd MMM", Locale.US);

            Date d = input.parse(isoDate);
            return output.format(d);
        } catch (Exception e) {
            return isoDate;
        }
    }


    private Date stripTime(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
