package inc.visor.voom.app.admin.pricing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.pricing.dto.VehicleTypeDto;

public class EditPricingFragment extends Fragment {

    private EditPricingViewModel mViewModel;
    private Spinner spinnerVehicleType;
    private EditText etPrice;
    private Button btnSubmit;

    private List<VehicleTypeDto> vehicleTypeList = new ArrayList<>();

    public static EditPricingFragment newInstance() {
        return new EditPricingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_pricing, container, false);

        spinnerVehicleType = root.findViewById(R.id.spinnerVehicleType);
        etPrice = root.findViewById(R.id.etNewPrice);
        btnSubmit = root.findViewById(R.id.btnSubmitPrice);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditPricingViewModel.class);

        setupObservers();
        setupListeners();

        mViewModel.fetchVehicleTypes();
    }

    private void setupObservers() {
        mViewModel.getVehicleTypesData().observe(getViewLifecycleOwner(), types -> {
            if (types != null) {
                this.vehicleTypeList = types;

                List<String> typeNames = types.stream()
                        .map(VehicleTypeDto::getType)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, typeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVehicleType.setAdapter(adapter);
            }
        });

        mViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VehicleTypeDto selected = vehicleTypeList.get(position);
                etPrice.setText(String.valueOf(selected.getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSubmit.setOnClickListener(v -> {
            String priceText = etPrice.getText().toString().trim();
            int selectedPosition = spinnerVehicleType.getSelectedItemPosition();

            if (!priceText.isEmpty() && selectedPosition != AdapterView.INVALID_POSITION) {
                try {
                    Double newPrice = Double.parseDouble(priceText);
                    VehicleTypeDto selectedDto = vehicleTypeList.get(selectedPosition);

                    mViewModel.updatePrice(selectedDto.getId(), selectedDto.getType(), newPrice);
                } catch (NumberFormatException e) {
                    etPrice.setError("Invalid price format");
                }
            } else {
                Toast.makeText(getContext(), "Please enter a price", Toast.LENGTH_SHORT).show();
            }
        });
    }
}