package inc.visor.voom.app.driver.activate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import inc.visor.voom.app.R;

public class AccountActivationFragment extends Fragment {

    private AccountActivationViewModel viewModel;
    private String token;

    public AccountActivationFragment() {
        super(R.layout.fragment_account_activation2);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AccountActivationViewModel.class);

        token = getArguments() != null ? getArguments().getString("token") : null;

        EditText password1 = view.findViewById(R.id.etPassword1);
        EditText password2 = view.findViewById(R.id.etPassword2);
        Button btnActivate = view.findViewById(R.id.btnActivate);

        btnActivate.setOnClickListener(v -> {

            String p1 = password1.getText().toString();
            String p2 = password2.getText().toString();

            if (TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
                Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!p1.equals(p2)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.activate(token, p1, p2);
        });

        observe();
    }

    private void observe() {

        viewModel.isSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Account activated", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this)
                        .navigate(R.id.loginFragment);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Activation failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
