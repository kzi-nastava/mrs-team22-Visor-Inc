package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import inc.visor.voom.app.R;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.AuthenticationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivationFragment extends Fragment {

    Button submit;
    AuthenticationApi authenticationApi;


    public AccountActivationFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("DeepLink", "onCreateView called");
        return inflater.inflate(R.layout.fragment_account_activation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("DeepLink", "AccountActivationFragment loaded");

        authenticationApi = RetrofitClient.getInstance().create(AuthenticationApi.class);

        submit = view.findViewById(R.id.activate_account);

        submit.setOnClickListener(v -> {
            assert getArguments() != null;
            final String token = getArguments().getString("token");
            Log.d("VERIFICATION_TOKEN", token);
            authenticationApi.verifyUser(token).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Navigation.findNavController(view).navigate(R.id.action_accountActivationFragment_to_loginFragment);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
        });

    }
}
