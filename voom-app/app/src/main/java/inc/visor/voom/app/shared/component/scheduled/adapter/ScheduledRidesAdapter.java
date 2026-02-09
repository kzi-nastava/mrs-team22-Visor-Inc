package inc.visor.voom.app.shared.component.scheduled.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.model.enums.RideStatus;

public class ScheduledRidesAdapter extends RecyclerView.Adapter<ScheduledRidesAdapter.ViewHolder> {

    private List<RideHistoryDto> rides;
    private boolean isDriver;
    private OnCancelClickListener cancelListener;

    public interface OnCancelClickListener {
        void onCancel(RideHistoryDto ride);
    }

    public ScheduledRidesAdapter(List<RideHistoryDto> rides, boolean isDriver, OnCancelClickListener listener) {
        this.rides = rides;
        this.isDriver = isDriver;
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sheduled_ride_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideHistoryDto ride = rides.get(position);
        String time = ride.getRideRequest().getScheduledTime().split("T")[1].substring(0, 5);
        holder.tvTime.setText(time);
        holder.tvStart.setText(ride.getRideRoute().getPickup().getAddress());
        holder.tvEnd.setText(ride.getRideRoute().getDropOff().getAddress());
        holder.tvName.setText(ride.getRideRequest().getCreator().getPerson().getFirstName());

        holder.tvPrice.setText(String.format(Locale.getDefault(), "Price: $%.2f", ride.getRideRequest().getCalculatedPrice()));
        holder.tvDistance.setText(String.format(Locale.getDefault(), "Distance: %.2f km", ride.getRideRoute().getTotalDistanceKm()));

        if (ride.getRideRequest().getVehicleType() != null) {
            holder.tvVehicle.setText(String.format("Vehicle: %s", ride.getRideRequest().getVehicleType().getType()));
        }

        holder.tvStatus.setText(String.format("Status: %s", ride.getStatus().toString()));

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = holder.expandedLayout.getVisibility() == View.VISIBLE;
            holder.expandedLayout.setVisibility(expanded ? View.GONE : View.VISIBLE);
        });

        if (!isDriver) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            if (ride.getStatus() == RideStatus.SCHEDULED) {
                holder.btnCancel.setEnabled(true);
                holder.btnCancel.setAlpha(1.0f);
                holder.btnCancel.setOnClickListener(v -> cancelListener.onCancel(ride));
            } else {
                holder.btnCancel.setEnabled(false);
                holder.btnCancel.setAlpha(0.5f);
                holder.btnCancel.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), "This ride is already cancelled", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return rides.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime, tvStart, tvEnd;
        TextView tvPrice, tvDistance, tvVehicle, tvStatus, tvName;
        View expandedLayout;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStart = itemView.findViewById(R.id.tvStartAddr);
            tvEnd = itemView.findViewById(R.id.tvEndAddr);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            expandedLayout = itemView.findViewById(R.id.expandedContent);
            btnCancel = itemView.findViewById(R.id.btnCancelRide);
        }
    }
}
