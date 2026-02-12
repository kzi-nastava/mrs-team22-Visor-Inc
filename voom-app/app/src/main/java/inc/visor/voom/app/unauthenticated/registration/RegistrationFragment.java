package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import inc.visor.voom.app.R;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.AuthenticationApi;
import inc.visor.voom.app.shared.dto.authentication.RegistrationDto;
import inc.visor.voom.app.shared.dto.authentication.UserDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    RegistrationViewModel viewModel;
    AuthenticationApi authenticationApi;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authenticationApi = RetrofitClient.getInstance().create(AuthenticationApi.class);

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        viewModel.getRedirectToLogin().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {
                Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_loginFragment);
            }
        });

        viewModel.getRegistrationComplete().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {

                final String firstName = viewModel.getFirstName().getValue();
                final String lastName = viewModel.getLastName().getValue();
                final LocalDateTime birthDate = viewModel.getBirthDate().getValue();
                final String email = viewModel.getEmail().getValue();
                final String password = viewModel.getPassword().getValue();
                final String address = viewModel.getAddress().getValue();
                final String phoneNumber = viewModel.getPhoneNumber().getValue();

                final RegistrationDto dto = new RegistrationDto();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                dto.setFirstName(firstName);
                dto.setLastName(lastName);
                dto.setBirthDate(birthDate.format(formatter));
                dto.setEmail(email);
                dto.setPassword(password);
                dto.setAddress(address);
                dto.setPhoneNumber(phoneNumber);
                dto.setUserType("USER");

                Log.d("TEST", "DTO: " + dto);

                authenticationApi.register(dto).enqueue(new Callback<UserDto>() {
                    @Override
                    public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }
                        Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_loginFragment);
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }
}