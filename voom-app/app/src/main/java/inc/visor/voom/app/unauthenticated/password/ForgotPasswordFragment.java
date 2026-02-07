package inc.visor.voom.app.unauthenticated.password;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import inc.visor.voom.app.shared.dto.authentication.ForgotPasswordDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {

    ForgotPasswordViewModel viewModel;
    TextInputEditText emailInput;
    Button buttonSubmit;
    AuthenticationApi authenticationApi;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authenticationApi = RetrofitClient.getInstance().create(AuthenticationApi.class);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        emailInput = view.findViewById(R.id.email_input);

        setupEmailInput();

        buttonSubmit = view.findViewById(R.id.forgot_password_button);
        buttonSubmit.setOnClickListener(v -> {
            final ForgotPasswordDto dto = new ForgotPasswordDto();
            final String email = viewModel.getEmail().getValue();
            dto.setEmail(email);
            authenticationApi.forgotPassword(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
        });
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
                updateSubmitButtonState();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    private boolean isFormValid() {
        String email = viewModel.getEmail().getValue();

        return email != null && !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updateSubmitButtonState() {
        buttonSubmit.setEnabled(isFormValid());
    }
}