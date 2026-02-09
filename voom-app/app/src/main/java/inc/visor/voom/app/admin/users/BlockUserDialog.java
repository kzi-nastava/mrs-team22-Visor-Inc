package inc.visor.voom.app.admin.users;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BlockUserDialog extends DialogFragment {

    public interface OnConfirmListener {
        void onConfirm(String reason);
    }

    private OnConfirmListener listener;

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        EditText input = new EditText(getContext());
        input.setHint("Reason for blocking");

        return new AlertDialog.Builder(requireContext())
                .setTitle("Block User")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (!reason.isEmpty() && listener != null) {
                        listener.onConfirm(reason);
                    }
                })
                .create();
    }
}
