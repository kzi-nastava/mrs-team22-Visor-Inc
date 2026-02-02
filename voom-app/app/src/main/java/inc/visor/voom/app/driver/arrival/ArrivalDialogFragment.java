package inc.visor.voom.app.driver.arrival;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ArrivalDialogFragment extends DialogFragment {

    public interface Listener {
        void onStartRide();
    }

    private Listener listener;
    private String pickupAddress;

    public static ArrivalDialogFragment newInstance(String address) {
        ArrivalDialogFragment fragment = new ArrivalDialogFragment();
        Bundle args = new Bundle();
        args.putString("pickup", address);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        pickupAddress = getArguments() != null
                ? getArguments().getString("pickup")
                : "";

        return new AlertDialog.Builder(requireContext())
                .setTitle("Start ride")
                .setMessage("You have arrived at the pickup location.\n\n"
                        + pickupAddress)
                .setPositiveButton("Start ride", (dialog, which) -> {
                    if (listener != null) {
                        listener.onStartRide();
                    }
                })
                .setCancelable(false)
                .create();
    }
}
