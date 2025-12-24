package inc.visor.voom.app.unauthenticated.login;

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
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import inc.visor.voom.app.R;
import inc.visor.voom.app.unauthenticated.registration.RegistrationViewModel;

public class LoginFragment extends Fragment {

    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button buttonLogin;
    TextView buttonForgotPassword;
    TextView buttonRegister;
    LoginViewModel viewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);

        setupEmailInput();
        setupPasswordInput();

        buttonLogin = view.findViewById(R.id.login);

        buttonLogin.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_profileFragment));

        buttonForgotPassword = view.findViewById(R.id.forgot_password);

        buttonForgotPassword.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

        buttonRegister = view.findViewById(R.id.register);

        buttonRegister.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment));

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}