package inc.visor.voom.app.driver.profile;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;
import inc.visor.voom.app.databinding.FragmentDriverProfileBinding;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.user.profile.ChangePasswordDialogFragment;

public class DriverProfileFragment extends Fragment {

    private FragmentDriverProfileBinding binding;
    private DriverProfileViewModel viewModel;
    private DataStoreManager storeManager;

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

        storeManager = DataStoreManager.getInstance(this.getContext());

        observeViewModel();
        setupListeners();
        setupVehicleTypeDropdown();

        viewModel.loadProfile();
        viewModel.loadVehicle();
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
                value -> binding.etVehicleType.setText(value, false));

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

        binding.btnChangeVehicleInfo.setOnClickListener(v ->
                viewModel.saveVehicle(
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

        viewModel.getVehicleUpdated().observe(getViewLifecycleOwner(), updated -> {
            if (Boolean.TRUE.equals(updated)) {
                Snackbar.make(
                        requireView(),
                        "Vehicle information updated",
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });

    }

    private void setupListeners() {

        binding.btnLogout.setOnClickListener(v -> {
            storeManager.clearUserData();
            requireActivity().runOnUiThread(() -> {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host)
                        .navigate(R.id.unauthenticatedHomeFragment, null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.main_nav_graph, true)
                                        .build()
                        );
            });
        });

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
        binding.etVehicleType.setThreshold(0);

        binding.etVehicleType.setOnClickListener(v ->
                binding.etVehicleType.showDropDown()
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
