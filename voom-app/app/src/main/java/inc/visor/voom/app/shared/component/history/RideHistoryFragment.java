package inc.visor.voom.app.shared.component.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import inc.visor.voom.app.R;

public class RideHistoryFragment extends Fragment {

    private RideHistoryViewModel mViewModel;
    private RecyclerView rvRides;
    private TextView tvEmptyState;
    private RideHistoryAdapter adapter;

    public RideHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RideHistoryViewModel.class);

        mViewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides == null || rides.isEmpty()) {
                rvRides.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
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

        mViewModel.loadRideHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        rvRides = view.findViewById(R.id.history_recycler_view);
        tvEmptyState = view.findViewById(R.id.history_empty_state);

        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.dispose();
    }
}