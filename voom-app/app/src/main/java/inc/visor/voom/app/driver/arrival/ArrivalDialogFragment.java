package inc.visor.voom.app.driver.arrival;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;

public class ArrivalDialogFragment extends DialogFragment {

    public interface Listener {
        void onAcceptRide();
        void onCancelRide(String cancellationReason);
    }

    private Listener listener;
    private String pickupAddress;
    private TextInputEditText cancellationReasonInput;

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

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_arrival_dialog, null);

        // Set up views
        TextView pickupAddressTextView = dialogView.findViewById(R.id.pickup_address);
        cancellationReasonInput = dialogView.findViewById(R.id.cancellation_reason_input);
        Button acceptRideButton = dialogView.findViewById(R.id.ride_start);
        Button cancelRideButton = dialogView.findViewById(R.id.ride_cancel);

        pickupAddressTextView.setText("You have arrived at the pickup location.\n\n" + pickupAddress);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set up button click listeners
        acceptRideButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAcceptRide();
            }
            dialog.dismiss();
        });

        cancelRideButton.setOnClickListener(v -> {
            if (listener != null) {
                String reason = cancellationReasonInput.getText() != null
                        ? cancellationReasonInput.getText().toString().trim()
                        : "";
                listener.onCancelRide(reason);
            }
            dialog.dismiss();
        });

        return dialog;
    }
}