package inc.visor.voom.app.shared.component.history;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.users.dto.UserProfileDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.dto.ride.RatingSummaryDto;
import inc.visor.voom.app.user.tracking.dto.RatingRequestDto;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {

    private List<RideHistoryDto> rides;
    private FragmentManager fragmentManager;

    private String currentUserEmail;

    private String userRole;

//    DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public RideHistoryAdapter(List<RideHistoryDto> rides, FragmentManager parentFragmentManager) {
        this.rides = rides;
        this.fragmentManager = parentFragmentManager;
        this.currentUserEmail = DataStoreManager.getInstance().getUserEmail().blockingGet();
        this.userRole = DataStoreManager.getInstance().getUserRole().blockingGet();
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

        if ("cancelled".equalsIgnoreCase(ride.getStatus().toString())) {
            holder.layoutCancellationDetails.setVisibility(View.VISIBLE);
            holder.tvCancelledBy.setText("Cancelled by: " + ride.getCancelledBy());
            holder.tvCancelReason.setText("Reason: " + ride.getRideRequest().reason);
        } else {
            holder.layoutCancellationDetails.setVisibility(View.GONE);
        }

        boolean isExpanded = ride.isExpanded();
        holder.expandedContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.headerLayout.setOnClickListener(v -> {
            ride.setExpanded(!ride.isExpanded());
            notifyItemChanged(position);
        });

        holder.btnOpenRoute.setOnClickListener(v -> {
            openRouteOnMap(ride);
        });

        if (canRate(ride)) {
            holder.btnOpenRating.setVisibility(View.VISIBLE);
            holder.btnOpenRating.setOnClickListener(v -> {
                showRatingDialog(v.getContext(), ride);
            });
        } else {
            holder.btnOpenRating.setVisibility(View.GONE);
        }
    }

    private void openRouteOnMap(RideHistoryDto ride) {
        RideHistoryDialog dialog = RideHistoryDialog.newInstance(ride);
        dialog.show(fragmentManager, "RideHistoryDialog");
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
        LinearLayout layoutCancellationDetails;
        TextView tvCancelledBy, tvCancelReason;
        Button btnOpenRoute, btnOpenRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            headerLayout = itemView.findViewById(R.id.headerLayout);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStartAddr = itemView.findViewById(R.id.tvStartAddr);
            tvEndAddr = itemView.findViewById(R.id.tvEndAddr);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivDriverIcon = itemView.findViewById(R.id.ivDriverIcon);

            expandedContent = itemView.findViewById(R.id.expandedContent);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            tvPetTransport = itemView.findViewById(R.id.tvPetTransport);
            tvBabyTransport = itemView.findViewById(R.id.tvBabyTransport);

            layoutCancellationDetails = itemView.findViewById(R.id.layoutCancellationDetails);
            tvCancelledBy = itemView.findViewById(R.id.tvCancelledBy);
            tvCancelReason = itemView.findViewById(R.id.tvCancelReason);

            btnOpenRoute = itemView.findViewById(R.id.btnOpenRoute);
            btnOpenRating = itemView.findViewById(R.id.btnOpenRating);
        }
    }

    private void showRatingDialog(Context context, RideHistoryDto ride) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rate_ride, null);
        bottomSheetDialog.setContentView(view);

        RatingBar ratingDriver = view.findViewById(R.id.ratingDriver);
        RatingBar ratingCar = view.findViewById(R.id.ratingCar);
        EditText etComment = view.findViewById(R.id.etReviewComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview);

        btnSubmit.setOnClickListener(v -> {
            int driverRating = (int) ratingDriver.getRating();
            int carRating = (int) ratingCar.getRating();
            String comment = etComment.getText().toString();

            RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);
            RatingRequestDto dto = new RatingRequestDto();
            dto.setVehicleRating(carRating);
            dto.setDriverRating(driverRating);
            dto.setComment(comment);
            rideApi.rateRide(ride.getId(), dto).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        RatingSummaryDto newLocalRating = new RatingSummaryDto();
                        UserProfileDto dto = new UserProfileDto();
                        dto.setEmail(currentUserEmail);
                        dto.setFirstName("By");
                        dto.setLastName("Yourself");
                        newLocalRating.setRater(dto);
                        newLocalRating.setMessage(comment);
                        newLocalRating.setCreatedAt(LocalDateTime.now().toString());
                        newLocalRating.setDriverRating(driverRating);
                        newLocalRating.setVehicleRating(carRating);
                        ride.getRatings().add(newLocalRating);
                        Toast.makeText(view.getContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(view.getContext(), "Failed to submit", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                    Log.e("RATING_ERROR", "Network failure", t);
                    Toast.makeText(view.getContext(), "Network error", Toast.LENGTH_LONG).show();
                }
            });

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private boolean canRate(RideHistoryDto ride) {

        if (ride.getFinishedAt() == null || !"finished".equalsIgnoreCase(ride.getStatus().toString())) {
            return false;
        }

        if (!userRole.equals("USER")) {
            return false;
        }

        for (RatingSummaryDto rating : ride.getRatings()) {
            if (rating.getRater().getEmail().equals(currentUserEmail)) {
                return false;
            }
        }

        try {
            LocalDateTime finishedDate = LocalDateTime.parse(ride.getFinishedAt());
            return finishedDate.isAfter(LocalDateTime.now().minusDays(3));

        } catch (Exception e) {
            return false;
        }
    }

}

