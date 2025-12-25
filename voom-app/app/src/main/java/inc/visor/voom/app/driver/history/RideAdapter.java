package inc.visor.voom.app.driver.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import inc.visor.voom.app.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> rides = new ArrayList<Ride>();


    SimpleDateFormat formatter =
            new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.ENGLISH);

    public void submitList(List<Ride> list) {
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
        }

        void bind(Ride ride) {

            Passenger mainPassenger = ride.getPassengers()
                    .stream()
                    .filter(Passenger::isOrderedRide)
                    .findFirst()
                    .orElse(ride.getPassengers().get(0));

            tvPassenger.setText(mainPassenger.getFullName());
            tvRoute.setText(ride.getSource() + " → " + ride.getDestination());
            tvTime.setText(
                    ride.getEndTime().isEmpty()
                            ? ride.getStartTime()
                            : ride.getStartTime() + "–" + ride.getEndTime()
            );

            tvDate.setText(formatter.format(ride.getDate()));
            boolean showDateHeader;

            if (getAbsoluteAdapterPosition() == 0) {
                showDateHeader = true;
            } else {
                Date current = normalizeDate(ride.getDate());
                Date previous = normalizeDate(rides.get(getAbsoluteAdapterPosition() - 1).getDate());
                showDateHeader = !current.equals(previous);
            }
            tvDate.setVisibility(showDateHeader ? View.VISIBLE : View.GONE);

            tvDistance.setText("Distance: 2km");
            tvPrice.setText("Price: " + ride.getPrice());
            tvStatus.setText("Status: " + ride.getStatus());
            tvPanic.setText("Panic: " + (ride.isPanic() ? "✔\uFE0F" : "✖\uFE0F"));
            StringBuilder passengersText = new StringBuilder("Passengers: \n");
            for (Passenger p : ride.getPassengers()) {
                passengersText.append(String.format("%s   ", p.getFullName()));
            }

            tvPassengers.setText(passengersText.toString());


            expandedLayout.setVisibility(
                    ride.isExpanded() ? View.VISIBLE : View.GONE
            );

            itemView.setOnClickListener(v -> {
                ride.setExpanded(!ride.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }

    }

    private Date normalizeDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}

