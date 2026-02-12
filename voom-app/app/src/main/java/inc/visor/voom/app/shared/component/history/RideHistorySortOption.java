package inc.visor.voom.app.shared.component.history;

import androidx.annotation.NonNull;

public class RideHistorySortOption {
    public final String label;
    public final String value;

    public RideHistorySortOption(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return label;
    }
}
