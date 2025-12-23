package inc.visor.voom.app.user.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

        binding.btnChangePassword.setOnClickListener(v -> {
            new ChangePasswordDialogFragment().show(getParentFragmentManager(), "ChangePasswordDialog");
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
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v ->
                viewModel.onSaveClicked(
                        binding.etFirstName.getText().toString(),
                        binding.etLastName.getText().toString(),
                        binding.etEmail.getText().toString()
                )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}