package inc.visor.voom.app.shared.component.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.dto.ride.ComplaintSummaryDto;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {

    private List<ComplaintSummaryDto> complaints;

    public ComplaintsAdapter(List<ComplaintSummaryDto> complaints) {
        this.complaints = complaints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintSummaryDto complaint = complaints.get(position);

        // Set complaint message
        if (complaint.getMessage() != null && !complaint.getMessage().isEmpty()) {
            holder.tvComplaintMessage.setText(complaint.getMessage());
        } else {
            holder.tvComplaintMessage.setText("No message provided");
        }

        // Set complaint time
        if (complaint.getTime() != null) {
            try {
                DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .optionalStart()
                        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                        .optionalEnd()
                        .toFormatter();

                LocalDateTime dateTime = LocalDateTime.parse(complaint.getTime().toString(), inputFormatter);
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                holder.tvComplaintTime.setText(formattedTime);
            } catch (Exception e) {
                holder.tvComplaintTime.setText(complaint.getTime().toString());
            }
        } else {
            holder.tvComplaintTime.setVisibility(View.GONE);
        }

        // Set reporter name
        if (complaint.getReporter() != null) {
            String reporterName = complaint.getReporter().getFirstName() + " " + complaint.getReporter().getLastName();
            holder.tvComplaintReporter.setText("Reported by: " + reporterName);
        } else {
            holder.tvComplaintReporter.setText("Anonymous report");
        }
    }

    @Override
    public int getItemCount() {
        return complaints != null ? complaints.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintMessage, tvComplaintTime, tvComplaintReporter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComplaintMessage = itemView.findViewById(R.id.tvComplaintMessage);
            tvComplaintTime = itemView.findViewById(R.id.tvComplaintTime);
            tvComplaintReporter = itemView.findViewById(R.id.tvComplaintReporter);
        }
    }
}