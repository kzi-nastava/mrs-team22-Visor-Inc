package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.time.LocalDateTime;
import java.util.Date;

import inc.visor.voom.app.R;
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

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        final String firstName = viewModel.getFirstName().getValue();
        final String lastName = viewModel.getLastName().getValue();
        final LocalDateTime birthDate = viewModel.getBirthDate().getValue();
        final String email = viewModel.getEmail().getValue();
        final String password = viewModel.getPassword().getValue();
        final String address = viewModel.getAddress().getValue();
        final String phoneNumber = viewModel.getPhoneNumber().getValue();

        RegistrationDto dto = new RegistrationDto();

        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setBirthDate(birthDate);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setAddress(address);
        dto.setPhoneNumber(phoneNumber);

        viewModel.getRegistrationComplete().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {
                authenticationApi.register(dto).enqueue(new Callback<UserDto>() {
                    @Override
                    public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }
                        Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_loginFragment);
                    }

                    @Override
                    public void onFailure(Call<UserDto> call, Throwable t) {

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