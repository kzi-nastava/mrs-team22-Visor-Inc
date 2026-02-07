package inc.visor.voom.app.admin.tracking.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import inc.visor.voom.app.databinding.ItemPassengerBinding;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.ViewHolder> {

    private List<String> passengerNames = new ArrayList<>();

    public void setPassengers(List<String> names) {
        this.passengerNames = names != null ? names : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPassengerBinding binding = ItemPassengerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = passengerNames.get(position);
        holder.binding.tvPassengerName.setText(name);
    }

    @Override
    public int getItemCount() {
        return passengerNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPassengerBinding binding;

        public ViewHolder(ItemPassengerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
