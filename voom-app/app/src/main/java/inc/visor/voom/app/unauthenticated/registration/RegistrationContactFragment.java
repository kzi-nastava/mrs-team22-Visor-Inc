package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

import inc.visor.voom.app.R;

public class RegistrationContactFragment extends Fragment {

    RegistrationViewModel viewModel;
    TextInputEditText phoneNumberInput;
    TextInputEditText addressInput;
    Button buttonPrevious;
    Button buttonSignup;

    public RegistrationContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumberInput = view.findViewById(R.id.phone_number_input);
        addressInput = view.findViewById(R.id.address_input);

        viewModel = new ViewModelProvider(requireParentFragment().requireParentFragment()).get(RegistrationViewModel.class);

        setupPhoneNumberInput();
        setupAddressInput();

        buttonPrevious = view.findViewById(R.id.fragment_registration_contact_previous);

        buttonPrevious.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_registrationContactFragment_to_registrationAccountFragment));

        buttonSignup = view.findViewById(R.id.fragment_registration_contact_signup);

        buttonSignup.setOnClickListener(v -> viewModel.setRegistrationComplete(true));

    }

    private void setupPhoneNumberInput() {
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String phoneNumber = editable.toString();
                if (phoneNumber.isEmpty()) {
                    phoneNumberInput.setError("Email is required");
                } else if (phoneNumber.length() < 10) {
                    phoneNumberInput.setError("Phone number must be at least 10 numbers long");
                } else {
                    phoneNumberInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setPhoneNumber(charSequence.toString());
            }
        });
    }

    private void setupAddressInput() {
        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String address = editable.toString();
                if (address.isEmpty()) {
                    addressInput.setError("Email is required");
                } else if (address.length() < 10) {
                    addressInput.setError("Phone number must be at least 10 numbers long");
                } else {
                    addressInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setAddress(charSequence.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_contact, container, false);
    }
}