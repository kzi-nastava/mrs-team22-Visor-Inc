package inc.visor.voom.app.unauthenticated.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import inc.visor.voom.app.R;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.AuthenticationApi;
import inc.visor.voom.app.shared.dto.authentication.LoginDto;
import inc.visor.voom.app.shared.dto.authentication.TokenDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button buttonLogin;
    TextView buttonForgotPassword;
    TextView buttonRegister;
    LoginViewModel viewModel;
    AuthenticationApi authenticationApi;
    DataStoreManager dataStoreManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authenticationApi = RetrofitClient.getInstance().create(AuthenticationApi.class);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);

        setupEmailInput();
        setupPasswordInput();

        buttonLogin = view.findViewById(R.id.login);
        buttonLogin.setEnabled(false);

        dataStoreManager = DataStoreManager.getInstance(this.getContext());

        buttonLogin.setOnClickListener(v -> {
            final LoginDto dto = new LoginDto();
            final String email = viewModel.getEmail().getValue();
            final String password = viewModel.getPassword().getValue();

            dto.setEmail(email);
            dto.setPassword(password);

            authenticationApi.login(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<TokenDto> call, @NonNull Response<TokenDto> response) {
                    Log.d("TEST", "Request: " + call + " " + response);
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }
                    final TokenDto dto = response.body();
                    dataStoreManager.saveUserData(dto);
                    if (Objects.equals(dto.getUser().getRole(), "USER")) {
                        if (isAdded() && !isDetached()) {
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainUserFragment);
                        }
                    } else if (Objects.equals(dto.getUser().getRole(), "DRIVER")) {
                        if (isAdded() && !isDetached()) {
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainDriverFragment);
                        }
                    } else if (Objects.equals(dto.getUser().getRole(), "ADMIN")) {
                        if (isAdded() && !isDetached()) {
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainAdminFragment);
                        }
                    } else {
                        dataStoreManager.clearUserData();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TokenDto> call, @NonNull Throwable t) {
                }
            });
        });

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
                updateLoginButtonState();
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
                updateLoginButtonState();
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

    private boolean isFormValid() {
        String email = viewModel.getEmail().getValue();
        String password = viewModel.getPassword().getValue();

        boolean isEmailValid = email != null && !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isPasswordValid = password != null && !password.isEmpty() && password.length() >= 8;

        return isEmailValid && isPasswordValid;
    }

    private void updateLoginButtonState() {
        buttonLogin.setEnabled(isFormValid());
    }
}