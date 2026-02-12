package inc.visor.voom.app.shared.component.scheduled;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.component.scheduled.adapter.ScheduledRidesAdapter;

public class ScheduledRidesFragment extends Fragment {

    private ScheduledRidesViewModel mViewModel;
    private RecyclerView rvRides;
    private TextView tvEmptyState;
    private ScheduledRidesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduled_rides, container, false);

        rvRides = view.findViewById(R.id.rvScheduledRides);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduledRidesViewModel.class);

        boolean isDriver = checkIfUserIsDriver();

        mViewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides == null || rides.isEmpty()) {
                rvRides.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvRides.setVisibility(View.VISIBLE);

                adapter = new ScheduledRidesAdapter(rides, isDriver, ride -> mViewModel.cancelRide(ride));
                rvRides.setAdapter(adapter);
            }
        });

        mViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.loadScheduledRides();
    }

    private boolean checkIfUserIsDriver() {
        return DataStoreManager.getInstance().getUserRole().blockingGet().equals("DRIVER");
    }
}