package inc.visor.voom.app.driver.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentDriverProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverProfileViewModel.class);

        binding.btnChangePassword.setOnClickListener(v ->
                new ChangePasswordDialogFragment()
                        .show(getParentFragmentManager(), "ChangePasswordDialog")
        );

        observeViewModel();
        setupListeners();
        setupVehicleTypeDropdown();

        viewModel.loadProfile();
    }

    private void observeViewModel() {

        viewModel.getFirstName().observe(getViewLifecycleOwner(),
                binding.etFirstName::setText);

        viewModel.getLastName().observe(getViewLifecycleOwner(),
                binding.etLastName::setText);

        viewModel.getEmail().observe(getViewLifecycleOwner(),
                binding.etEmail::setText);

        viewModel.getAddress().observe(getViewLifecycleOwner(),
                binding.etAddress::setText);

        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(),
                binding.etPhoneNumber::setText);

        viewModel.getVehicleModel().observe(getViewLifecycleOwner(),
                binding.etVehicleModel::setText);

        viewModel.getVehicleType().observe(getViewLifecycleOwner(),
                binding.etVehicleType::setText);

        viewModel.getLicensePlate().observe(getViewLifecycleOwner(),
                binding.etLicensePlate::setText);

        viewModel.getNumberOfSeats().observe(getViewLifecycleOwner(),
                value -> binding.etNumberOfSeats.setText(
                        value != null ? String.valueOf(value) : ""
                ));

        viewModel.isBabyTransportAllowed().observe(getViewLifecycleOwner(),
                value -> binding.cbBabyTransport.setChecked(Boolean.TRUE.equals(value)));

        viewModel.isPetTransportAllowed().observe(getViewLifecycleOwner(),
                value -> binding.cbPetTransport.setChecked(Boolean.TRUE.equals(value)));

        viewModel.getFullName().observe(getViewLifecycleOwner(),
                binding.txtFullName::setText);


        viewModel.getProfileUpdated().observe(getViewLifecycleOwner(), updated -> {
            if (Boolean.TRUE.equals(updated)) {
                com.google.android.material.snackbar.Snackbar
                        .make(binding.getRoot(),
                                "Profile successfully updated",
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupListeners() {

        binding.btnSavePersonalInfo.setOnClickListener(v ->
                viewModel.onSaveClicked(
                        binding.etFirstName.getText().toString(),
                        binding.etLastName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etAddress.getText().toString(),
                        binding.etPhoneNumber.getText().toString(),
                        binding.etVehicleModel.getText().toString(),
                        binding.etVehicleType.getText().toString(),
                        binding.etLicensePlate.getText().toString(),
                        binding.etNumberOfSeats.getText() != null &&
                                !binding.etNumberOfSeats.getText().toString().isEmpty()
                                ? Integer.parseInt(binding.etNumberOfSeats.getText().toString())
                                : null,
                        binding.cbBabyTransport.isChecked(),
                        binding.cbPetTransport.isChecked()
                )
        );
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
