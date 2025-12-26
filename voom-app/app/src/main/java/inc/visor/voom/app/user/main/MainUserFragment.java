package inc.visor.voom.app.user.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.profile.ChangePasswordDialogFragment;
import inc.visor.voom.app.user.profile.ProfileViewModel;

public class MainUserFragment extends Fragment {

    private ProfileViewModel profileViewModel; //TODO remove

    public MainUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        //TODO remove
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getChangeFragment().observe(getViewLifecycleOwner(), isChange -> {
            if (isChange) {
                Navigation.findNavController(view).navigate(R.id.action_mainUserFragment_to_mainDriverFragment);
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_user, container, false);
    }
}