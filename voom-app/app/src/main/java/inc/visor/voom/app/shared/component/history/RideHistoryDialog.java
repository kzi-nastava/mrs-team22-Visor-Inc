package inc.visor.voom.app.shared.component.history;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.DialogFragment;

import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.shared.dto.RideHistoryDto;

public class RideHistoryDialog extends DialogFragment {

    private RideHistoryDto ride;

    public static RideHistoryDialog newInstance(RideHistoryDto ride) {
        RideHistoryDialog fragment = new RideHistoryDialog();
        Bundle args = new Bundle();
        args.putParcelable("ride", (Parcelable) ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ride = (RideHistoryDto) getArguments().getParcelable("ride");
        }
    }

}
