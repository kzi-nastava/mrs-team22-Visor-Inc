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

        viewModel.getRegistrationComplete().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {
                RegistrationDto dto = new RegistrationDto();

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