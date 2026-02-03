package inc.visor.voom.app.user.home.dialog;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jspecify.annotations.NonNull;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.helper.SimpleTextWatcher;

public class FavoriteRouteNameDialog extends DialogFragment {

    public interface OnRouteNameEntered {
        void onConfirm(String name);
    }

    private OnRouteNameEntered listener;

    public void setListener(OnRouteNameEntered listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        var inflater = requireActivity().getLayoutInflater();
        var view = inflater.inflate(R.layout.dialog_favorite_route, null);

        TextInputLayout inputLayout = view.findViewById(R.id.textInputLayout);
        TextInputEditText input = view.findViewById(R.id.routeNameInput);
        MaterialButton saveBtn = view.findViewById(R.id.saveButton);
        MaterialButton cancelBtn = view.findViewById(R.id.cancelButton);

        // Enable Save only if valid
        input.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                boolean valid = text.length() >= 2;

                saveBtn.setEnabled(valid);

                if (!valid && text.length() > 0) {
                    inputLayout.setError("Minimum 2 characters");
                } else {
                    inputLayout.setError(null);
                }
            }
        });

        saveBtn.setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (listener != null) {
                listener.onConfirm(name);
            }
            dismiss();
        });

        cancelBtn.setOnClickListener(v -> dismiss());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }
}
