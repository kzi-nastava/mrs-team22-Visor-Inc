package inc.visor.voom.app.admin.driver_approve;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import inc.visor.voom.app.R;

public class VehicleApproveFragment extends Fragment {

    private VehicleApproveViewModel viewModel;
    private String requestId;

    public VehicleApproveFragment() {
        super(R.layout.fragment_vehicle_approve);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(VehicleApproveViewModel.class);

        requestId = getArguments().getString("requestId");

        TextView txtDriver = view.findViewById(R.id.txtDriver);
        TextView txtModel = view.findViewById(R.id.txtModel);
        TextView txtPlate = view.findViewById(R.id.txtPlate);
        TextView txtSeats = view.findViewById(R.id.txtSeats);
        TextView txtBaby = view.findViewById(R.id.txtBaby);
        TextView txtPet = view.findViewById(R.id.txtPet);
        ProgressBar progress = view.findViewById(R.id.progress);
        Button btnApprove = view.findViewById(R.id.btnApprove);
        Button btnReject = view.findViewById(R.id.btnReject);

        viewModel.isLoading().observe(getViewLifecycleOwner(),
                isLoading -> progress.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getRequest().observe(getViewLifecycleOwner(), req -> {
            if (req != null) {
                txtDriver.setText("Driver: " + req.getDriverFullName());
                txtModel.setText("Model: " + req.getModel());
                txtPlate.setText("License Plate: " + req.getLicensePlate());
                txtSeats.setText("Seats: " + req.getNumberOfSeats());
                txtBaby.setText("Baby Seat: " + (req.isBabySeat() ? "Yes" : "No"));
                txtPet.setText("Pet Friendly: " + (req.isPetFriendly() ? "Yes" : "No"));
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();

                view.postDelayed(() -> {
                    NavHostFragment
                            .findNavController(this)
                            .navigate(R.id.unauthenticatedHomeFragment);
                }, 1500);

            }
        });


        btnApprove.setOnClickListener(v -> viewModel.approve(requestId));
        btnReject.setOnClickListener(v -> viewModel.reject(requestId));

        viewModel.loadRequest(requestId);
    }
}
