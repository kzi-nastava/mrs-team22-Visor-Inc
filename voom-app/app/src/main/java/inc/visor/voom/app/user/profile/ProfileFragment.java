package inc.visor.voom.app.user.profile;

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
import inc.visor.voom.app.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment().requireParentFragment()).get(ProfileViewModel.class);

        binding.btnChangePassword.setOnClickListener(v -> {
            new ChangePasswordDialogFragment().show(getParentFragmentManager(), "ChangePasswordDialog");
        });

        binding.btnChangeDriverProfile.setOnClickListener(v -> {
            viewModel.setChangeFragment(true);
        });

        observeViewModel();
        setupListeners();
    }

    private void observeViewModel() {
        viewModel.getFirstName().observe(getViewLifecycleOwner(),
                value -> binding.etFirstName.setText(value));

        viewModel.getLastName().observe(getViewLifecycleOwner(),
                value -> binding.etLastName.setText(value));

        viewModel.getEmail().observe(getViewLifecycleOwner(),
                value -> binding.etEmail.setText(value));

        viewModel.getAdress().observe(getViewLifecycleOwner(),
                value -> binding.etAddress.setText(value));

        viewModel.getPhoneNumeber().observe(getViewLifecycleOwner(),
                value -> binding.etPhoneNumber.setText(value));

    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v ->
                viewModel.onSaveClicked(
                        String.valueOf(binding.etFirstName.getText()),
                        String.valueOf(binding.etLastName.getText()),
                        String.valueOf(binding.etEmail.getText()),
                        String.valueOf(binding.etAddress.getText()),
                        String.valueOf(binding.etPhoneNumber.getText())
                )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}