package inc.visor.voom.app.driver.create;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

import inc.visor.voom.app.databinding.FragmentCreateDriverBinding;

public class CreateDriverFragment extends Fragment {

    private FragmentCreateDriverBinding binding;
    private CreateDriverViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentCreateDriverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CreateDriverViewModel.class);

        setupVehicleTypeDropdown();
        setupObservers();
        setupListeners();
        setupBirthDatePicker();
    }

    private void setupListeners() {

        binding.btnCreateDriver.setOnClickListener(v ->
                viewModel.createDriver(
                        binding.etEmail.getText().toString(),
                        binding.etFirstName.getText().toString(),
                        binding.etLastName.getText().toString(),
                        binding.etBirthDate.getText().toString(),
                        binding.etPhoneNumber.getText().toString(),
                        binding.etAddress.getText().toString(),
                        binding.etVehicleModel.getText().toString(),
                        binding.etVehicleType.getText().toString(),
                        binding.etLicensePlate.getText().toString(),
                        binding.etNumberOfSeats.getText() != null
                                && !binding.etNumberOfSeats.getText().toString().isEmpty()
                                ? Integer.parseInt(binding.etNumberOfSeats.getText().toString())
                                : null,
                        binding.cbBabyTransport.isChecked(),
                        binding.cbPetTransport.isChecked()
                )
        );
    }

    private void setupBirthDatePicker() {

        binding.etBirthDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {

                        String formatted = String.format(
                                Locale.getDefault(),
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayOfMonth
                        );

                        binding.etBirthDate.setText(formatted);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });
    }

    private void setupObservers() {

        viewModel.isLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.btnCreateDriver.setEnabled(!Boolean.TRUE.equals(loading));
            binding.progressBar.setVisibility(
                    Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE
            );
        });

        viewModel.isDriverCreated().observe(getViewLifecycleOwner(), created -> {
            if (Boolean.TRUE.equals(created)) {

                Snackbar.make(
                        requireView(),
                        "Driver successfully created",
                        Snackbar.LENGTH_SHORT
                ).show();

                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(
                        requireView(),
                        error,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
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

    private void clearForm() {
        binding.etEmail.setText("");
        binding.etFirstName.setText("");
        binding.etLastName.setText("");
        binding.etPhoneNumber.setText("");
        binding.etAddress.setText("");
        binding.etVehicleModel.setText("");
        binding.etVehicleType.setText("");
        binding.etLicensePlate.setText("");
        binding.etNumberOfSeats.setText("");
        binding.cbBabyTransport.setChecked(false);
        binding.cbPetTransport.setChecked(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
