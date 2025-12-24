package inc.visor.voom.app.driver.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;
import inc.visor.voom.app.databinding.FragmentDriverProfileBinding;
import inc.visor.voom.app.user.profile.ChangePasswordDialogFragment;


public class DriverProfileFragment extends Fragment {

    private FragmentDriverProfileBinding binding;

    private DriverProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle saveInstanceState
    ) {
        binding = FragmentDriverProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle saveInstanceState
    ) {
        super.onViewCreated(view, saveInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverProfileViewModel.class);

        binding.btnChangePassword.setOnClickListener(v -> {
            new ChangePasswordDialogFragment().show(getParentFragmentManager(), "ChangePasswordDialog");
        });

        observeViewModel();
        setupListeners();
        setupVehicleTypeDropdown();
    }

    private void observeViewModel() {

        viewModel.getFirstName().observe(getViewLifecycleOwner(),
                value -> binding.etFirstName.setText(value));

        viewModel.getLastName().observe(getViewLifecycleOwner(),
                value -> binding.etLastName.setText(value));

        viewModel.getEmail().observe(getViewLifecycleOwner(),
                value -> binding.etEmail.setText(value));

        viewModel.getAddress().observe(getViewLifecycleOwner(),
                value -> binding.etAddress.setText(value));

        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(),
                value -> binding.etPhoneNumber.setText(value));

        viewModel.getVehicleModel().observe(getViewLifecycleOwner(),
                value -> binding.etVehicleModel.setText(value));

        viewModel.getVehicleType().observe(getViewLifecycleOwner(),
                value -> binding.etVehicleType.setText(value));

        viewModel.getLicensePlate().observe(getViewLifecycleOwner(),
                value -> binding.etLicensePlate.setText(value));

        viewModel.getNumberOfSeats().observe(getViewLifecycleOwner(),
                value -> binding.etNumberOfSeats.setText(
                        value != null ? String.valueOf(value) : ""
                ));

        viewModel.isBabyTransportAllowed().observe(getViewLifecycleOwner(),
                value -> binding.cbBabyTransport.setChecked(
                        value != null && value
                ));

        viewModel.isPetTransportAllowed().observe(getViewLifecycleOwner(),
                value -> binding.cbPetTransport.setChecked(
                        value != null && value
                ));
    }

    private void setupListeners() {

        binding.btnSave.setOnClickListener(v ->
                viewModel.onSaveClicked(
                        String.valueOf(binding.etFirstName.getText()),
                        String.valueOf(binding.etLastName.getText()),
                        String.valueOf(binding.etEmail.getText()),
                        String.valueOf(binding.etAddress.getText()),
                        String.valueOf(binding.etPhoneNumber.getText()),
                        String.valueOf(binding.etVehicleModel.getText()),
                        String.valueOf(binding.etVehicleType.getText()),
                        String.valueOf(binding.etLicensePlate.getText()),
                        binding.etNumberOfSeats.getText() != null &&
                                !binding.etNumberOfSeats.getText().toString().isEmpty()
                                ? Integer.parseInt(binding.etNumberOfSeats.getText().toString())
                                : null,
                        binding.cbBabyTransport.isChecked(),
                        binding.cbPetTransport.isChecked()
                )
        );
    }


    public DriverProfileFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupVehicleTypeDropdown() {
        String[] vehicleTypes = {"STANDARD", "LUXURY", "VAN"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                vehicleTypes
        );

        binding.etVehicleType.setAdapter(adapter);
    }



}