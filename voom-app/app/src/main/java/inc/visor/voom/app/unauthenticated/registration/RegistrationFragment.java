package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inc.visor.voom.app.R;

public class RegistrationFragment extends Fragment {

    RegistrationViewModel viewModel;
    private static final String TAG = "REG_DEBUG"; // Step 1: Define a Tag


    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        viewModel.getRegistrationComplete().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {
                Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_profileFragment);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }
}