package inc.visor.voom.app.driver.history;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair; // Crucial for Range Picker
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.history.adapters.RideAdapter;

public class DriverRideHistoryFragment extends Fragment {


    private DriverRideHistoryViewModel mViewModel;
    private RideAdapter adapter;

    private Date startDate = null;
    private Date endDate = null;

    private boolean asc = false;

    private TextInputEditText bookingDateEditText; // Declare here

    public static DriverRideHistoryFragment newInstance() {
        return new DriverRideHistoryFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_driver_ride_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rideRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RideAdapter();
        recyclerView.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(DriverRideHistoryViewModel.class);

        mViewModel.getRides().observe(getViewLifecycleOwner(), adapter::submitList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        bookingDateEditText = view.findViewById(R.id.booking_date_edit_text);

        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Dates");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        bookingDateEditText.setOnClickListener(v -> {
            datePicker.show(getChildFragmentManager(), "DATE_RANGE_PICKER");
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String startDateStr = dateFormat.format(new Date(selection.first));
            String endDateStr = dateFormat.format(new Date(selection.second));

            startDate = new Date(selection.first);
            endDate = new Date(selection.second);

            bookingDateEditText.setText(startDateStr + " - " + endDateStr);
        });

        datePicker.addOnDismissListener(dialog -> bookingDateEditText.clearFocus());

        Button applyBtn = view.findViewById(R.id.apply_btn);
        applyBtn.setOnClickListener(v -> {
            if (startDate != null && endDate != null)  {
                mViewModel.filter(startDate, endDate, asc);
            }
        });

        ImageButton sortButton = view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(v -> {
            asc = !asc;
            mViewModel.activateSort(asc);
            sortButton.setRotationX(sortButton.getRotationX() + 180f);
        });

        TextView clearFilters = view.findViewById(R.id.clearFiltersText);

        clearFilters.setOnClickListener(v -> {
            mViewModel.clearFilters(asc);
            bookingDateEditText.setText("");
            startDate = null;
            endDate = null;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DriverRideHistoryViewModel.class);
    }
}