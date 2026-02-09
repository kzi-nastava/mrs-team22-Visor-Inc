package inc.visor.voom.app.shared.component.history;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import inc.visor.voom.app.R;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.dto.ride.RideResponseDto;
import inc.visor.voom.app.shared.model.DriverLocationDto;
import inc.visor.voom.app.user.home.UserHomeViewModel;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryDialog extends DialogFragment {

    private RideApi rideApi;
    private RideHistoryDto ride;
    private RideHistoryDialogViewModel viewModel;

    // Views
    private TextView tvDriverName;
    private TextView tvDriverStatus;
    private TextView tvNoRatings;
    private TextView tvNoComplaints;
    private TextView tvTimeError;
    private RecyclerView rvRatings;
    private RecyclerView rvComplaints;
    private AutoCompleteTextView ddTime;
    private TextInputLayout tilScheduledTime;
    private TextInputEditText etScheduledTime;
    private Button btnScheduleRide;

    public static RideHistoryDialog newInstance(RideHistoryDto ride) {
        RideHistoryDialog fragment = new RideHistoryDialog();
        Bundle args = new Bundle();
        args.putSerializable("ride", ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ride = (RideHistoryDto) getArguments().getSerializable("ride");
        }
        viewModel = new RideHistoryDialogViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Dialog dialog = new Dialog(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ride_history_dialog, null);
        dialog.setContentView(view);

        rideApi = RetrofitClient.getInstance().create(RideApi.class);

        initializeViews(view);

        setupDriverInfo();
        setupRatings();
        setupComplaints();
        setupScheduling();
        setupRoutePoints();

        return dialog;
    }

    private void initializeViews(View view) {
        tvDriverName = view.findViewById(R.id.tvDriverName);
        tvDriverStatus = view.findViewById(R.id.tvDriverStatus);
        tvNoRatings = view.findViewById(R.id.tvNoRatings);
        tvNoComplaints = view.findViewById(R.id.tvNoComplaints);
        tvTimeError = view.findViewById(R.id.tv_time_error);

        rvRatings = view.findViewById(R.id.rvRatings);
        rvComplaints = view.findViewById(R.id.rvComplaints);

        ddTime = view.findViewById(R.id.dd_time);
        tilScheduledTime = view.findViewById(R.id.til_scheduled_time);
        etScheduledTime = view.findViewById(R.id.et_scheduled_time);
        btnScheduleRide = view.findViewById(R.id.btnScheduleRide);
    }

    private void setupDriverInfo() {
        if (ride != null && ride.getDriver() != null) {
            tvDriverName.setText(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
            tvDriverStatus.setText(ride.getDriver().getStatus() != null ? ride.getDriver().getStatus().toString() : "Active");
        }
    }

    private void setupRatings() {
        if (ride != null && ride.getRatings() != null && !ride.getRatings().isEmpty()) {
            RatingAdapter adapter = new RatingAdapter(ride.getRatings());
            rvRatings.setAdapter(adapter);
            rvRatings.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRatings.setVisibility(View.VISIBLE);
            tvNoRatings.setVisibility(View.GONE);
        } else {
            rvRatings.setVisibility(View.GONE);
            tvNoRatings.setVisibility(View.VISIBLE);
        }
    }

    private void setupComplaints() {
        if (ride != null && ride.getComplaints() != null && !ride.getComplaints().isEmpty()) {
            ComplaintsAdapter adapter = new ComplaintsAdapter(ride.getComplaints());
            rvComplaints.setAdapter(adapter);
            rvComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
            rvComplaints.setVisibility(View.VISIBLE);
            tvNoComplaints.setVisibility(View.GONE);
        } else {
            rvComplaints.setVisibility(View.GONE);
            tvNoComplaints.setVisibility(View.VISIBLE);
        }
    }

    private void setupScheduling() {
        String[] timeOptions = {"Now", "Later"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                timeOptions
        );
        ddTime.setAdapter(adapter);

        ddTime.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) { // Now
                tilScheduledTime.setVisibility(View.GONE);
                tvTimeError.setVisibility(View.GONE);
                viewModel.getScheduledTime().setValue(null);
            } else { // Later
                tilScheduledTime.setVisibility(View.VISIBLE);
            }
        });

        etScheduledTime.setOnClickListener(v -> showTimePicker());

        btnScheduleRide.setOnClickListener(v -> confirmRide());

        viewModel.getScheduledTimeValid().observe(this, isValid -> {
            tvTimeError.setVisibility(isValid ? View.GONE : View.VISIBLE);
            btnScheduleRide.setEnabled(isValid);
        });
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select ride time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            etScheduledTime.setText(time);
            viewModel.getScheduledTime().setValue(time);

            // Validate time (within next 5 hours)
            validateScheduledTime(hour, minute);
        });

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }

    private void validateScheduledTime(int hour, int minute) {
        // Simple validation - you can make this more sophisticated
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar scheduled = java.util.Calendar.getInstance();
        scheduled.set(java.util.Calendar.HOUR_OF_DAY, hour);
        scheduled.set(java.util.Calendar.MINUTE, minute);

        long diffMillis = scheduled.getTimeInMillis() - now.getTimeInMillis();
        long diffHours = diffMillis / (1000 * 60 * 60);

        boolean isValid = diffHours >= 0 && diffHours <= 5;
        viewModel.getScheduledTimeValid().setValue(isValid);
    }

    private void setupRoutePoints() {
        if (ride != null && ride.getRideRoute() != null && ride.getRideRoute().getRoutePoints() != null) {
            viewModel.getRoutePoints().setValue(ride.getRideRoute().getRoutePoints());

            // TODO: Setup map view here if you want to display the route
            // This would require initializing your map library (OSM or Google Maps)
        }
    }

    private String buildScheduledDate() {
        String timeString = viewModel.getScheduledTime().getValue();
        if (timeString == null) {
            return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toString();
        }

        String[] parts = timeString.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If scheduled time is before now, assume it's for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Convert to ISO 8601 format
        LocalDateTime dateTime = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toString();
    }

    private void confirmRide() {
        if (ride == null || ride.getRideRoute() == null) {
            Toast.makeText(getContext(), "Invalid ride data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build schedule object
        String selectedTimeOption = ddTime.getText().toString();
        RideRequestDto.Schedule schedule;

        if ("Later".equals(selectedTimeOption)) {
            schedule = new RideRequestDto.Schedule();
            schedule.setType(RideHistoryDialogViewModel.ScheduleType.LATER.toString());
            schedule.setStartAt(buildScheduledDate());
        } else {
            schedule = new RideRequestDto.Schedule();
            schedule.setType(RideHistoryDialogViewModel.ScheduleType.NOW.toString());
            schedule.setStartAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toString());
        }

        // Build route with points
        List<RideRequestDto.Point> routePoints = ride.getRideRoute().getRoutePoints().stream()
                .map(p -> {
                    RideRequestDto.Point dto = new RideRequestDto.Point();
                    dto.setLat(p.getLat());
                    dto.setLng(p.getLng());
                    dto.setOrderIndex(p.getOrderIndex());
                    dto.setType(p.getPointType().toString());
                    dto.setAddress(p.getAddress());
                    return dto;
                })
                .collect(Collectors.toList());

        RideRequestDto.Route route = new RideRequestDto.Route();
        route.setPoints(routePoints);

        // Build preferences
        RideRequestDto.Preferences preferences = new RideRequestDto.Preferences();
        preferences.setPets(ride.getRideRequest().isPetTransport());
        preferences.setBaby(ride.getRideRequest().isBabyTransport());

        List<DriverLocationDto> freeDriversSnapshot = new ArrayList<>();
        if (ride.getDriver() != null && !ride.getRideRoute().getRoutePoints().isEmpty()) {
            DriverLocationDto driverSnapshot = new DriverLocationDto();
            driverSnapshot.setDriverId(ride.getDriver().getId());
            driverSnapshot.setLat(ride.getRideRoute().getRoutePoints().get(0).getLat());
            driverSnapshot.setLng(ride.getRideRoute().getRoutePoints().get(0).getLng());
            freeDriversSnapshot.add(driverSnapshot);
        }

        RideRequestDto payload = new RideRequestDto();
        payload.setRoute(route);
        payload.setSchedule(schedule);
        payload.setVehicleTypeId(ride.getRideRequest().getVehicleType().getId());
        payload.setPreferences(preferences);
        payload.setLinkedPassengers(ride.getRideRequest().getLinkedPassengerEmails());
        payload.setFreeDriversSnapshot(freeDriversSnapshot);

        btnScheduleRide.setEnabled(false);
        btnScheduleRide.setText("Scheduling...");

        rideApi.createRideRequest(payload)
                .enqueue(new Callback<RideRequestResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<RideRequestResponseDto> call,
                            Response<RideRequestResponseDto> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Log.e("RIDE", "Request failed");
                            return;
                        }

                        RideRequestResponseDto res = response.body();

                        if ("ACCEPTED".equals(res.status)
                                && res.driver != null) {

                            android.widget.Toast.makeText(
                                    requireContext(),
                                    "Ride accepted. Driver: "
                                            + res.driver.firstName
                                            + " "
                                            + res.driver.lastName,
                                    android.widget.Toast.LENGTH_LONG
                            ).show();


                        } else {
                            android.widget.Toast.makeText(
                                    requireContext(),
                                    "No drivers available",
                                    android.widget.Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RideRequestResponseDto> call,
                            Throwable t
                    ) {
                        Log.e("RIDE", "Network error", t);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
