package inc.visor.voom.app.shared.component.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.RideHistoryDto;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {

    private List<RideHistoryDto> rides;

    public RideHistoryAdapter(List<RideHistoryDto> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_history_card, parent, false);
        return new RideHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideHistoryDto ride = rides.get(position);

        String isoTime = ride.getStartedAt();

        DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        LocalDateTime dateTime = LocalDateTime.parse(isoTime, inputFormatter);

        String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        holder.tvTime.setText(formattedTime);
        holder.tvStartAddr.setText(ride.getRideRoute().getPickup().getAddress());
        holder.tvEndAddr.setText(ride.getRideRoute().getDropOff().getAddress());
        holder.tvUserName.setText(ride.getRideRequest().getCreator().getEmail());

        holder.tvPrice.setText(String.format("$%.2f", ride.getRideRequest().getCalculatedPrice()));
        holder.tvDistance.setText(String.format("%.1f km", ride.getRideRequest().getRideRoute().getTotalDistanceKm()));
        holder.tvVehicle.setText(ride.getRideRequest().getVehicleType().type);
        holder.tvStatus.setText(ride.getStatus().toString());

        holder.tvPetTransport.setText(ride.getRideRequest().isPetTransport() ? "Yes" : "No");
        holder.tvBabyTransport.setText(ride.getRideRequest().isBabyTransport() ? "Yes" : "No");

        // Handle cancellation details visibility
        if ("cancelled".equalsIgnoreCase(ride.getStatus().toString())) {
            holder.layoutCancellationDetails.setVisibility(View.VISIBLE);
            holder.tvCancelledBy.setText("Cancelled by: " + ride.getCancelledBy());
            holder.tvCancelReason.setText("Reason: " + ride.getRideRequest().reason);
        } else {
            holder.layoutCancellationDetails.setVisibility(View.GONE);
        }

        // Handle expand/collapse functionality
        boolean isExpanded = ride.isExpanded(); // Add this field to your RideHistory model
        holder.expandedContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Click listener for header to toggle expansion
        holder.headerLayout.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position);
        });

        // Open route button click listener
        holder.btnOpenRoute.setOnClickListener(v -> {
            // Open map with route
            openRouteOnMap(ride);
        });
    }

    // Helper method to open route on map
    private void openRouteOnMap(RideHistoryDto ride) {

    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Header elements
        TextView tvTime, tvStartAddr, tvEndAddr, tvUserName;
        ImageView ivDriverIcon;
        LinearLayout headerLayout;

        // Expanded content elements
        View expandedContent;
        TextView tvPrice, tvDistance, tvVehicle, tvStatus;
        TextView tvPetTransport, tvBabyTransport;

        // Cancellation details
        LinearLayout layoutCancellationDetails;
        TextView tvCancelledBy, tvCancelReason;

        // Buttons
        Button btnOpenRoute;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize header elements
            headerLayout = itemView.findViewById(R.id.headerLayout);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStartAddr = itemView.findViewById(R.id.tvStartAddr);
            tvEndAddr = itemView.findViewById(R.id.tvEndAddr);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivDriverIcon = itemView.findViewById(R.id.ivDriverIcon);

            // Initialize expanded content
            expandedContent = itemView.findViewById(R.id.expandedContent);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            // Initialize transport options
            tvPetTransport = itemView.findViewById(R.id.tvPetTransport);
            tvBabyTransport = itemView.findViewById(R.id.tvBabyTransport);

            // Initialize cancellation details
            layoutCancellationDetails = itemView.findViewById(R.id.layoutCancellationDetails);
            tvCancelledBy = itemView.findViewById(R.id.tvCancelledBy);
            tvCancelReason = itemView.findViewById(R.id.tvCancelReason);

            // Initialize buttons
            btnOpenRoute = itemView.findViewById(R.id.btnOpenRoute);
        }
    }

}

