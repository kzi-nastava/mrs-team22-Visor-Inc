package inc.visor.voom.app.shared.component.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.ride.RatingSummaryDto;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<RatingSummaryDto> ratings;

    public RatingAdapter(List<RatingSummaryDto> ratings) {
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatingSummaryDto rating = ratings.get(position);

        // Set rating message
        if (rating.getMessage() != null && !rating.getMessage().isEmpty()) {
            holder.tvMessage.setText(rating.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        // Set driver rating
        holder.tvDriverRating.setText(String.format("Driver rating: %.1f", (float) rating.getDriverRating()));

        // Set vehicle rating
        holder.tvVehicleRating.setText(String.format("Vehicle rating: %.1f", (float) rating.getVehicleRating()));

        // Set rater name
        if (rating.getRater() != null) {
            String raterName = rating.getRater().getFirstName() + " " + rating.getRater().getLastName();
            holder.tvRaterName.setText(raterName);
        } else {
            holder.tvRaterName.setText("Anonymous");
        }
    }

    @Override
    public int getItemCount() {
        return ratings != null ? ratings.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDriverRating, tvVehicleRating, tvRaterName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvRatingMessage);
            tvDriverRating = itemView.findViewById(R.id.tvDriverRating);
            tvVehicleRating = itemView.findViewById(R.id.tvVehicleRating);
            tvRaterName = itemView.findViewById(R.id.tvRaterName);
        }
    }
}