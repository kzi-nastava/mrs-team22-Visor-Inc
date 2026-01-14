package inc.visor.voom.app.user.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import inc.visor.voom.app.R;
import inc.visor.voom.app.databinding.DialogChangePasswordBinding;

public class ChangePasswordDialogFragment extends DialogFragment {

    private DialogChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        binding = DialogChangePasswordBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel = new ViewModelProvider(this)
                .get(ChangePasswordViewModel.class);

        observeViewModel();
        setupListeners();
        setupPasswordValidation();

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext()
                    .getResources()
                    .getDisplayMetrics()
                    .widthPixels * 0.8);

            getDialog().getWindow().setLayout(
                    width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void setupListeners() {
        binding.btnConfirm.setOnClickListener(v ->
                viewModel.changePassword(
                        binding.etNewPassword.getText().toString(),
                        binding.etConfirmPassword.getText().toString()
                )
        );
    }

    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                showSuccessMessage();
                dismiss();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setupPasswordValidation() {

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.etNewPassword.addTextChangedListener(watcher);
        binding.etConfirmPassword.addTextChangedListener(watcher);

        validatePasswords();
    }

    private void validatePasswords() {
        String newPassword = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        boolean hasMinLength = newPassword.length() >= 8;
        boolean hasUppercase = newPassword.matches(".*[A-Z].*");
        boolean hasLowercase = newPassword.matches(".*[a-z].*");
        boolean hasNumber = newPassword.matches(".*\\d.*");
        boolean passwordsMatch = newPassword.equals(confirmPassword);

        if (!newPassword.isEmpty() && !(hasMinLength && hasUppercase && hasLowercase && hasNumber)) {
            binding.newPasswordLayout.setError(
                    "Must contain 8 characters, uppercase, lowercase and number"
            );
        } else {
            binding.newPasswordLayout.setError(null);
        }

        if (!confirmPassword.isEmpty() && !passwordsMatch) {
            binding.confirmPasswordLayout.setError("Passwords do not match");
        } else {
            binding.confirmPasswordLayout.setError(null);
        }

        boolean enableConfirm =
                hasMinLength &&
                        hasUppercase &&
                        hasLowercase &&
                        hasNumber &&
                        passwordsMatch;

        binding.btnConfirm.setEnabled(enableConfirm);
    }

    private void showSuccessMessage() {
        if (getActivity() != null) {
            com.google.android.material.snackbar.Snackbar
                    .make(
                            getActivity().findViewById(android.R.id.content),
                            "Password successfully changed",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    )
                    .show();
        }
    }



}
