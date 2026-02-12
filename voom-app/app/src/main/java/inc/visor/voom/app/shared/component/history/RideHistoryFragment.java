package inc.visor.voom.app.shared.component.history;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import inc.visor.voom.app.R;

public class RideHistoryFragment extends Fragment {

    private RideHistoryViewModel mViewModel;
    private RecyclerView rvRides;
    private TextView tvEmptyState;
    private RideHistoryAdapter adapter;
    private TextInputEditText dateFromInput;
    private TextInputEditText dateToInput;
    private final DateTimeFormatter screenFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    private boolean isDateAscending;
    private AutoCompleteTextView sortType;

    private List<RideHistorySortOption> rideHistorySortOptions = List.of(
            new RideHistorySortOption("Newest first", "DATE_DESC"),
            new RideHistorySortOption("Oldest first", "DATE_ASC"),

            new RideHistorySortOption("Price: low → high", "PRICE_ASC"),
            new RideHistorySortOption("Price: high → low", "PRICE_DESC"),

            new RideHistorySortOption("Shortest distance", "DISTANCE_ASC"),
            new RideHistorySortOption("Longest distance", "DISTANCE_DESC"),

            new RideHistorySortOption("Status ascending", "STATUS_ASC"),
            new RideHistorySortOption("Status descending", "STATUS_DESC")
    );

    public RideHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RideHistoryViewModel.class);
        dateFromInput = view.findViewById(R.id.date_from_input);
        dateToInput = view.findViewById(R.id.date_to_input);

        mViewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides == null || rides.isEmpty()) {
                rvRides.setVisibility(View.GONE);
                rvRides.setAdapter(null);
                tvEmptyState.setVisibility(View.VISIBLE);
            } else if (rvRides.getAdapter() != null) {
                adapter.updateRides(rides);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvRides.setVisibility(View.VISIBLE);
                adapter = new RideHistoryAdapter(rides, getChildFragmentManager());
                rvRides.setAdapter(adapter);
            }
        });

        mViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.get_column().setValue("DATE");
        mViewModel.get_order().setValue("DESC");
        mViewModel.get_startDate().setValue(null);
        mViewModel.get_endDate().setValue(null);
        mViewModel.loadRideHistory();
        setupDateFromInput();
        setupDateToInput();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        rvRides = view.findViewById(R.id.history_recycler_view);
        tvEmptyState = view.findViewById(R.id.history_empty_state);

        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));

        sortType = view.findViewById(R.id.sort_type);

        ArrayAdapter<RideHistorySortOption> adapter = new ArrayAdapter<RideHistorySortOption>(getContext(), android.R.layout.simple_dropdown_item_1line, rideHistorySortOptions);

        sortType.setAdapter(adapter);
        sortType.setThreshold(1);
        sortType.setText(rideHistorySortOptions.get(0).label, false);
        sortType.setOnItemClickListener((parent, v, position, id) -> {
            RideHistorySortOption selected = (RideHistorySortOption) parent.getItemAtPosition(position);

            String value = selected.value;

            String[] parts = value.split("_");
            String field = parts[0];
            String direction = parts[1];

            mViewModel.get_column().setValue(field);
            mViewModel.get_order().setValue(direction);
            mViewModel.loadRideHistory();
        });

        setupShakeDetector();

        return view;
    }

    private void setupDateFromInput() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select start date")
                        .build();

        dateFromInput.setOnClickListener(v ->
                datePicker.show(getParentFragmentManager(), "DATE_FROM_PICKER")
        );

        datePicker.addOnPositiveButtonClickListener(selection -> {
            fromDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().atStartOfDay();

            mViewModel.get_startDate().setValue(formatter.format(fromDate));
            dateFromInput.setText(screenFormatter.format(fromDate));
            mViewModel.loadRideHistory();
        });

    }

    private void setupDateToInput() {
        dateToInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select end date")
                            .build();

            datePicker.show(getParentFragmentManager(), "DATE_TO_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {

                toDate = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate().atStartOfDay().plusDays(1L);

                mViewModel.get_endDate().setValue(formatter.format(toDate));
                dateToInput.setText(screenFormatter.format(toDate));
                mViewModel.loadRideHistory();
            });
        });
    }

    private void setupShakeDetector() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                shakeDetector = new ShakeDetector();
                shakeDetector.setOnShakeListener(() -> {
                    isDateAscending = !isDateAscending;

                    String sortOrder = isDateAscending ? "ASC" : "DESC";
                    String sortLabel = isDateAscending ? "Oldest first" : "Newest first";

                    sortType.setText(sortLabel, false);

                    mViewModel.get_column().setValue("DATE");
                    mViewModel.get_order().setValue(sortOrder);
                    mViewModel.loadRideHistory();

                    Toast.makeText(requireContext(),"Sorted by date: " + sortLabel,Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(requireContext(), "Accelerometer not available on this device",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResume() {
        super.onResume();
        // Register shake detector when fragment is visible
        if (sensorManager != null && accelerometer != null && shakeDetector != null) {
            sensorManager.registerListener(
                    shakeDetector,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.dispose();
    }
}