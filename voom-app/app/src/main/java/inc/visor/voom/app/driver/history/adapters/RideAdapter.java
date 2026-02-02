package inc.visor.voom.app.driver.history.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;

import inc.visor.voom.app.driver.history.models.RideHistoryModels.*;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<RideHistoryDto> rides = new ArrayList<>();

    private int expandedPosition = -1;


    public void submitList(List<RideHistoryDto> list) {
        rides.clear();
        rides.addAll(list);
        notifyDataSetChanged(); // OK for now
    }


    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        holder.bind(rides.get(position));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    class RideViewHolder extends RecyclerView.ViewHolder {

        TextView tvPassenger, tvRoute, tvTime, tvDate, tvDistance, tvPrice, tvStatus, tvPanic, tvPassengers;
        LinearLayout expandedLayout;

        RecyclerView passengerRecyclerView;
        PassengerAdapter passengerAdapter;

        RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassenger = itemView.findViewById(R.id.tvPassenger);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            tvPanic = itemView.findViewById(R.id.tvPanic);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);

            passengerRecyclerView =
                    itemView.findViewById(R.id.passengerRecyclerView);

            passengerRecyclerView.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext())
            );
            passengerRecyclerView.setNestedScrollingEnabled(false);

            passengerAdapter = new PassengerAdapter();
            passengerRecyclerView.setAdapter(passengerAdapter);

        }

        void bind(RideHistoryDto ride) {

            int currentPos = getAbsoluteAdapterPosition();
            boolean isExpanded = (currentPos == expandedPosition);


            User creator = ride.getRideRequest().getCreator();
            String creatorFullName = String.format("%s %s", creator.getPerson().getFirstName(), creator.getPerson().getFirstName());
            tvPassenger.setText(creatorFullName);

            String pickup = ride.getRideRoute().getPickup().getAddress();
            String dropoff;
            if (ride.getRideRoute().getDropOff() == null) {
                dropoff = "";
            } else {
                dropoff = ride.getRideRoute().getDropOff().getAddress();
            }
            String route = String.format("%s -> %s", pickup, dropoff);

            tvRoute.setText(route);

            String startTimeText;
            String endTimeText;
            String dateText;

            if (ride.getStartedAt().isBlank()) {
                startTimeText = "";
                dateText = "";
            } else {
                startTimeText = ride.getStartedAt().split("T")[1].substring(0, 5);
                dateText = ride.getStartedAt().split("T")[0].substring(0, 10);
            }

            if (ride.getFinishedAt().isBlank()) {
                endTimeText = "";
            } else {
                endTimeText = ride.getFinishedAt().split("T")[1].substring(0, 5);
            }

            String timeText = String.format("%s - %s", startTimeText, endTimeText);

            tvTime.setText(timeText);

            tvDate.setText(dateText);
            boolean showDateHeader;

            if (getAbsoluteAdapterPosition() == 0) {
                showDateHeader = true;
            } else {
                String current = dateText;
                String previous = rides.get(getAbsoluteAdapterPosition() - 1).getStartedAt().split("T")[0].substring(0, 10);
                showDateHeader = !current.equals(previous);
            }
            tvDate.setVisibility(showDateHeader ? View.VISIBLE : View.GONE);

            String distance = String.format("Distance: %.2f", ride.getRideRoute().getTotalDistanceKm());
            tvDistance.setText(distance);
            String price = String.format("Price: %.2f", ride.getRideRequest().getCalculatedPrice());
            tvPrice.setText(price);
            String status = String.format("Status: %s", ride.getStatus());
            tvStatus.setText(status);
            String panic = String.format("Panic: %s", ride.getStatus() == RideStatus.PANIC ? "✔️" : "✖️");
            tvPanic.setText(panic);


            tvPassengers.setText("Passengers: ");


            passengerAdapter.submitList(ride.getPassengers());

            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                int previousExpanded = expandedPosition;
                expandedPosition = isExpanded ? -1 : currentPos;

                notifyItemChanged(previousExpanded);
                notifyItemChanged(currentPos);
            });
        }

    }
}

