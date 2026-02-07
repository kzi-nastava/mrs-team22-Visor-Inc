package inc.visor.voom.app.shared.component;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import inc.visor.voom.app.databinding.FragmentUserStatisticsBinding;
import inc.visor.voom.app.user.statistics.dto.ReportDailyStatsDto;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;

public abstract class BaseStatisticsFragment extends Fragment {

    protected FragmentUserStatisticsBinding binding;

    protected Date fromDate;
    protected Date toDate;
    protected boolean loading = false;
    protected boolean isSuspended = false;

    protected final SimpleDateFormat apiFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    protected final SimpleDateFormat uiFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    protected final DecimalFormat kmFmt = new DecimalFormat("#0.00");
    protected final DecimalFormat moneyFmt = new DecimalFormat("#0.00");

    protected abstract void onGenerate(String from, String to);

    protected abstract String getMoneySummaryLabel();

    protected abstract String getMoneyChartLabel();

    protected abstract boolean canGenerateInternal();

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        setupDatePickers();
        setupChartsDefaults();
        updateUiState();
    }

    protected void setupDatePickers() {

        binding.fromInput.setOnClickListener(v -> openSingleDatePicker(true));
        binding.fromLayout.setEndIconOnClickListener(v -> openSingleDatePicker(true));

        binding.toInput.setOnClickListener(v -> openSingleDatePicker(false));
        binding.toLayout.setEndIconOnClickListener(v -> openSingleDatePicker(false));

        binding.btnGenerate.setOnClickListener(v -> {
            if (!canGenerate()) return;
            loading = true;
            updateUiState();
            onGenerate(apiFmt.format(fromDate), apiFmt.format(toDate));
        });
    }

    private void openSingleDatePicker(boolean isFrom) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isFrom ? "Select From date" : "Select To date")
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Date d = stripTime(new Date(selection));
            if (isFrom) {
                fromDate = d;
                binding.fromInput.setText(uiFmt.format(fromDate));
            } else {
                toDate = d;
                binding.toInput.setText(uiFmt.format(toDate));
            }
            validateRangeAndShowError();
            updateUiState();
        });

        picker.show(getParentFragmentManager(), isFrom ? "fromPicker" : "toPicker");
    }

    protected void populateUi(ReportResponseDto dto) {

        binding.txtTotalRides.setText(String.valueOf(dto.getTotalRides()));
        binding.txtTotalKm.setText(kmFmt.format(dto.getTotalKm()) + " km");
        binding.txtTotalMoney.setText(moneyFmt.format(dto.getTotalMoney()));
        double totalMoney = dto.getTotalMoney();

        long days = 1;
        if (fromDate != null && toDate != null) {
            long diff = toDate.getTime() - fromDate.getTime();
            days = (diff / (1000 * 60 * 60 * 24)) + 1;
            if (days <= 0) days = 1;
        }

        double average = totalMoney / days;

        binding.txtAverageMoney.setText(moneyFmt.format(average));


        binding.txtTotalMoneyLabel.setText(getMoneySummaryLabel());
        binding.txtMoneyChartTitle.setText(getMoneyChartLabel());

        List<ReportDailyStatsDto> stats =
                dto.getDailyStats() != null ? dto.getDailyStats() : new ArrayList<>();

        setupRidesChart(binding.chartRides, stats);
        setupKmChart(binding.chartKm, stats);
        setupMoneyChart(binding.chartMoney, stats, getMoneyChartLabel());

        loading = false;
        updateUiState();
    }

    protected void resetUi() {
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
                && !loading
                && canGenerateInternal();
    }

    protected void updateUiState() {
        validateRangeAndShowError();
        binding.btnGenerate.setEnabled(canGenerate());
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
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
}
