package inc.visor.voom.app.shared.component.history;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.Serializable;
import java.util.Locale;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.history.models.Ride;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import io.reactivex.rxjava3.disposables.Disposable;

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
            tvDriverStatus.setText(ride.getDriver().getStatus() != null ?
                    ride.getDriver().getStatus().toString() : "Active");
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

        btnScheduleRide.setOnClickListener(v -> scheduleRide());

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

    private void scheduleRide() {
        // TODO: Implement ride scheduling logic
        String scheduledTime = viewModel.getScheduledTime().getValue();

        if (scheduledTime != null) {
            // Schedule for later

            // rideApi.scheduleRide(ride.getRideRoute(), scheduledTime)...
        } else {
            // Schedule for now
            // rideApi.scheduleRide(ride.getRideRoute(), null)...
        }

        dismiss();
    }
}
