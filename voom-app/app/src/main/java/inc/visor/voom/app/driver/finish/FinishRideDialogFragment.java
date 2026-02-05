package inc.visor.voom.app.driver.finish;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FinishRideDialogFragment extends DialogFragment {

    public interface Listener {
        void onFinishRide();
    }

    private Listener listener;

    public static FinishRideDialogFragment newInstance(String address) {
        FinishRideDialogFragment fragment = new FinishRideDialogFragment();
        Bundle args = new Bundle();
        args.putString("destination", address);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String destinationAddress = getArguments() != null
                ? getArguments().getString("destination")
                : "";

        return new AlertDialog.Builder(requireContext())
                .setTitle("Finish ride")
                .setMessage("You have arrived at the dropoff location.\n\n"
                        + destinationAddress)
                .setPositiveButton("Finish ride", (dialog, which) -> {
                    if (listener != null) {
                        listener.onFinishRide();
                    }
                })
                .setCancelable(false)
                .create();
    }
}

