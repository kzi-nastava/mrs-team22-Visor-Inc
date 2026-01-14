package inc.visor.voom.app.user.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeViewModel();
        setupListeners();

        viewModel.loadProfile();

    }

    private void observeViewModel() {
        viewModel.getFirstName().observe(getViewLifecycleOwner(),
                binding.etFirstName::setText);

        viewModel.getLastName().observe(getViewLifecycleOwner(),
                binding.etLastName::setText);

        viewModel.getEmail().observe(getViewLifecycleOwner(),
                binding.etEmail::setText);

        viewModel.getAddress().observe(getViewLifecycleOwner(),
                binding.etAddress::setText);

        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(),
                binding.etPhoneNumber::setText);

        viewModel.getProfileUpdated().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                showProfileUpdatedMessage();
            }
        });

        viewModel.getFullName().observe(getViewLifecycleOwner(),
                binding.txtFullName::setText
        );


    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v ->
                viewModel.onSaveClicked(
                        binding.etFirstName.getText().toString(),
                        binding.etLastName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etAddress.getText().toString(),
                        binding.etPhoneNumber.getText().toString()
                )
        );

        binding.btnChangePassword.setOnClickListener(v ->
                new ChangePasswordDialogFragment()
                        .show(getParentFragmentManager(), "ChangePasswordDialog")
        );
    }

    private void showProfileUpdatedMessage() {
        com.google.android.material.snackbar.Snackbar
                .make(
                        binding.getRoot(),
                        "Profile successfully updated",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                )
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
