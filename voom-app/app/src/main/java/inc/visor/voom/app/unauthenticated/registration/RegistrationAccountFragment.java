package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import inc.visor.voom.app.R;

public class RegistrationAccountFragment extends Fragment {

    RegistrationViewModel viewModel;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputEditText repeatPasswordInput;
    Button buttonPrevious;
    Button buttonNext;

    public RegistrationAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        repeatPasswordInput = view.findViewById(R.id.repeat_password_input);

        viewModel = new ViewModelProvider(
                requireParentFragment()
        ).get(RegistrationViewModel.class);

        setupEmailInput();
        setupPasswordInput();
        setupRepeatPasswordInput();

        buttonPrevious = view.findViewById(R.id.fragment_registration_personal_previous);

        buttonPrevious.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_registrationAccountFragment_to_registrationPersonalFragment));

        buttonNext = view.findViewById(R.id.fragment_registration_personal_next);

        buttonNext.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_registrationAccountFragment_to_registrationContactFragment));
    }

    private void setupEmailInput() {
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String email = editable.toString();
                if (email.isEmpty()) {
                    emailInput.setError("Email is required");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Please enter a valid email address");
                } else {
                    emailInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setEmail(charSequence.toString());
            }
        });
    }

    private void setupPasswordInput() {
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String password = editable.toString();
                if (password.isEmpty()) {
                    passwordInput.setError("Password is required");
                } else if (password.length() < 8) {
                    passwordInput.setError("Password must be at least 8 characters");
                } else {
                    passwordInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setPassword(charSequence.toString());
            }
        });
    }

    private void setupRepeatPasswordInput() {
        repeatPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String password = viewModel.getPassword().getValue();
                final String repeatedPassword = editable.toString();
                if (repeatedPassword.isEmpty()) {
                    passwordInput.setError("Password is required");
                } else if (repeatedPassword.length() < 8) {
                    passwordInput.setError("Password must be at least 8 characters");
                } else if (!repeatedPassword.equals(password)) {
                    passwordInput.setError("Passwords must match");
                } else {
                    passwordInput.setError(null);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setRepeatPassword(charSequence.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_account, container, false);
    }
}