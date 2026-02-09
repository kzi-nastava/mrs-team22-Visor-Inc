package inc.visor.voom.app.admin.users;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;

public class BlockUserDialog extends DialogFragment {

    public interface OnConfirmListener {
        void onConfirm(String reason);
    }

    private OnConfirmListener listener;
    private String fullName;

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_block_user, null);

        TextInputLayout inputLayout = view.findViewById(R.id.inputLayoutReason);
        TextInputEditText etReason = view.findViewById(R.id.etReason);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirm);


        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String reason = etReason.getText() != null ?
                    etReason.getText().toString().trim() : "";

            if (reason.length() < 5) {
                inputLayout.setError("Minimum 5 characters required");
                return;
            }

            inputLayout.setError(null);

            if (listener != null) {
                listener.onConfirm(reason);
            }

            dialog.dismiss();
        });

        return dialog;
    }
}
