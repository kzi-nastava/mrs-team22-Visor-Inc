package inc.visor.voom.app.admin.statistics;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.*;

import java.text.SimpleDateFormat;
import java.util.*;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.statistics.dto.ReportResponseDto;
import inc.visor.voom.app.admin.users.api.UserApi;
import inc.visor.voom.app.admin.users.dto.UserProfileDto;
import inc.visor.voom.app.databinding.FragmentAdminStatisticsBinding;

import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import android.widget.AdapterView;
import android.widget.TextView;


public class AdminStatisticsFragment extends Fragment {

    private FragmentAdminStatisticsBinding binding;
    private AdminStatisticsViewModel viewModel;

    private UserApi userApi;
    private List<UserProfileDto> users = new ArrayList<>();
    private UserProfileDto selectedUser = null;


    private Date fromDate, toDate;

    public AdminStatisticsFragment() {
        super(R.layout.fragment_admin_statistics);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminStatisticsBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AdminStatisticsViewModel.class);


        userApi = RetrofitClient.getInstance().create(UserApi.class);
        loadUsers();

        setupDatePickers();

        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.setPinchZoom(true);
        binding.lineChart.setScaleEnabled(true);
        binding.lineChart.animateX(800);


        binding.generateButton.setOnClickListener(v -> generate());

        viewModel.getReport().observe(getViewLifecycleOwner(), this::populateUi);
    }

    private View createView(int position) {

        TextView tv = new TextView(requireContext());
        tv.setPadding(32, 24, 32, 24);
        tv.setTextSize(16f);

        UserProfileDto u = users.get(position);

        int iconRes;

        if (u == null) {
            tv.setText("System Overview");
            iconRes = R.drawable.ic_system;
        } else if ("DRIVER".equals(u.getUserRoleName())) {
            tv.setText(u.firstName + " " + u.lastName);
            iconRes = R.drawable.ic_car;
        } else {
            tv.setText(u.firstName + " " + u.lastName);
            iconRes = R.drawable.ic_driver;
        }


        Drawable icon = ContextCompat.getDrawable(requireContext(), iconRes);
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        tv.setCompoundDrawablePadding(16);

        return tv;
    }


    private void generate() {
        if (fromDate == null || toDate == null) return;

        String from = formatDate(fromDate);
        String to = formatDate(toDate);

        Long userId = null;
        Long driverId = null;

        if (selectedUser != null) {
            if ("DRIVER".equals(selectedUser.getUserRoleName())) {
                driverId = selectedUser.id;
            } else {
                userId = selectedUser.id;
            }
        }

        viewModel.loadReport(from, to, userId, driverId);
    }

    private void loadUsers() {
        Log.d("ADMIN", "loadUsers called");

        userApi.getUsers().enqueue(new Callback<List<UserProfileDto>>() {
            @Override
            public void onResponse(Call<List<UserProfileDto>> call,
                                   Response<List<UserProfileDto>> response) {
                Log.d("ADMIN", "Users count: " + response.body().size());

                if (response.isSuccessful() && response.body() != null) {

                    users.clear();
                    users.add(null);

                    users.addAll(response.body());

                    ArrayAdapter<UserProfileDto> adapter =
                            new ArrayAdapter<UserProfileDto>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    users
                            ) {

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return createView(position);
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    return createView(position);
                                }
                            };

                    binding.userSpinner.setAdapter(adapter);

                    binding.userSpinner.setAdapter(adapter);

                    binding.userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedUser = users.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<UserProfileDto>> call, Throwable t) { }
        });
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
