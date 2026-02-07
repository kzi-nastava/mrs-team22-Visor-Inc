package inc.visor.voom.app.admin.tracking.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import inc.visor.voom.app.R;;
import inc.visor.voom.app.databinding.ItemDriverBinding;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {

    private List<DriverSummaryDto> drivers = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final OnDriverClickListener listener;

    public interface OnDriverClickListener {
        void onDriverClick(DriverSummaryDto driver);
    }

    public DriverAdapter(OnDriverClickListener listener) {
        this.listener = listener;
    }

    public void setDrivers(List<DriverSummaryDto> drivers) {
        this.drivers = drivers;
        // When the list is updated, we reset selection or find the new index of the selected ID
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDriverBinding binding = ItemDriverBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverSummaryDto driver = drivers.get(position);

        holder.binding.tvDriverName.setText(driver.getFirstName() + " " + driver.getLastName());

        if ("SUSPENDED".equals(driver.getStatus())) {
            holder.binding.ivSuspendedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.binding.ivSuspendedIcon.setVisibility(View.GONE);
        }

        if (selectedPosition == position) {
            holder.binding.driverCard.setCardBackgroundColor(Color.parseColor("#6200EE")); // Your primary color
            holder.binding.tvDriverName.setTextColor(Color.WHITE);
            holder.binding.ivSuspendedIcon.setColorFilter(Color.WHITE);
        } else {
            holder.binding.driverCard.setCardBackgroundColor(Color.WHITE);
            holder.binding.tvDriverName.setTextColor(Color.BLACK);
            holder.binding.ivSuspendedIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            listener.onDriverClick(driver);
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDriverBinding binding;

        public ViewHolder(ItemDriverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}