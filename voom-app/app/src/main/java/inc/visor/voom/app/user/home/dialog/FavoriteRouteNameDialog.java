package inc.visor.voom.app.user.home.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import org.jspecify.annotations.NonNull;

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

        EditText input = new EditText(requireContext());
        input.setHint("Route name");

        return new AlertDialog.Builder(requireContext())
                .setTitle("Add to favorites")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty() && listener != null) {
                        listener.onConfirm(name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}

