package inc.visor.voom.app.driver.arrival;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;

public class ArrivalDialogFragment extends DialogFragment {

    public interface OnAcceptRideListener {
        void onAcceptRide();
    }

    public interface OnCancelRideListener {
        void onCancelRide(String cancellationReason);
    }

    private OnAcceptRideListener acceptListener;
    private OnCancelRideListener cancelListener;
    private String pickupAddress;
    private TextInputEditText cancellationReasonInput;
    private Button acceptButton;
    private Button cancelButton;

    public static ArrivalDialogFragment newInstance(String address) {
        ArrivalDialogFragment fragment = new ArrivalDialogFragment();
        Bundle args = new Bundle();
        args.putString("pickup", address);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAcceptRideListener(OnAcceptRideListener listener) {
        this.acceptListener = listener;
    }

    public void setOnCancelRideListener(OnCancelRideListener listener) {
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_arrival_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create();

        acceptButton = dialogView.findViewById(R.id.accept_ride_button);
        cancelButton = dialogView.findViewById(R.id.cancel_ride_button);
        cancellationReasonInput = dialogView.findViewById(R.id.cancellation_reason_input);

        pickupAddress = getArguments() != null
                ? getArguments().getString("pickup")
                : "";

        acceptButton.setOnClickListener(v -> {
            if (acceptListener != null) {
                acceptListener.onAcceptRide();
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            if (cancelListener != null) {
                String reason = cancellationReasonInput.getText() != null
                        ? cancellationReasonInput.getText().toString().trim()
                        : "";
                cancelListener.onCancelRide(reason);
            }
            dialog.dismiss();
        });

        return dialog;
    }
}
