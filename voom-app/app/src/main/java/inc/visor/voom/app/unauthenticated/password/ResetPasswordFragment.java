package inc.visor.voom.app.unauthenticated.password;

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
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.AuthenticationApi;
import inc.visor.voom.app.shared.dto.authentication.ResetPasswordDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    ResetPasswordViewModel viewModel;
    TextInputEditText passwordInput;
    TextInputEditText repeatPasswordInput;
    Button buttonSubmit;
    AuthenticationApi authenticationApi;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("DeepLink", "ResetPasswordFragment loaded");

        authenticationApi = RetrofitClient.getInstance().create(AuthenticationApi.class);

        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        passwordInput = view.findViewById(R.id.password_input);
        repeatPasswordInput = view.findViewById(R.id.repeat_password_input);

        setupPasswordInput();
        setupRepeatPasswordInput();

        buttonSubmit = view.findViewById(R.id.reset_password_button);
        buttonSubmit.setEnabled(false);
        buttonSubmit.setOnClickListener(v -> {
            final ResetPasswordDto dto = new ResetPasswordDto();
            assert getArguments() != null;
            final String token = getArguments().getString("token");
            dto.setPassword(viewModel.getPassword().getValue());
            dto.setConfirmPassword(viewModel.getRepeatPassword().getValue());
            dto.setToken(token);
            authenticationApi.resetPassword(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Navigation.findNavController(view).navigate(R.id.action_resetPasswordFragment_to_loginFragment);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
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
                updateSubmitButton();
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
                updateSubmitButton();
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
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    private boolean isFormValid() {
        String password = viewModel.getPassword().getValue();
        String confirmPassword = viewModel.getRepeatPassword().getValue();

        boolean isPasswordValid = password != null && !password.isEmpty() && password.length() >= 8;
        boolean isConfirmPasswordValid = confirmPassword != null && !confirmPassword.isEmpty() && confirmPassword.length() >= 8;

        return isConfirmPasswordValid && isPasswordValid && confirmPassword.equals(password);
    }

    private void updateSubmitButton() {
        buttonSubmit.setEnabled(isFormValid());
    }
}