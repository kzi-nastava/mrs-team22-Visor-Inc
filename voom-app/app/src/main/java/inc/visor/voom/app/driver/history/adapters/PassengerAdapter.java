package inc.visor.voom.app.driver.history.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.driver.history.models.Passenger;

public class PassengerAdapter
        extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private final List<Passenger> passengers = new ArrayList<>();

    public void submitList(List<Passenger> list) {
        passengers.clear();
        passengers.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public PassengerViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            PassengerViewHolder holder, int position) {

        holder.bind(passengers.get(position));
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        PassengerViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPassengerName);
        }

        void bind(Passenger passenger) {
            tvName.setText(passenger.getFullName());
        }
    }
}

