package inc.visor.voom.app.user.favorite_route.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteUI;

public class FavoriteRoutesAdapter
        extends ListAdapter<FavoriteRouteUI, FavoriteRoutesAdapter.ViewHolder> {

    public interface Listener {
        void onPick(FavoriteRouteUI route);
        void onDelete(FavoriteRouteUI route);
    }

    private final Listener listener;
    private long expandedId = -1;

    public FavoriteRoutesAdapter(Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FavoriteRouteUI> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FavoriteRouteUI>() {
                @Override
                public boolean areItemsTheSame(@NonNull FavoriteRouteUI oldItem,
                                               @NonNull FavoriteRouteUI newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull FavoriteRouteUI oldItem,
                                                  @NonNull FavoriteRouteUI newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_route_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRouteUI item = getItem(position);
        boolean expanded = item.id == expandedId;
        holder.bind(item, expanded);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final FavoriteRouteItemUI ui;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ui = new FavoriteRouteItemUI(itemView);

            itemView.setOnClickListener(v -> {
                FavoriteRouteUI item = getItem(getBindingAdapterPosition());

                if (expandedId == item.id) {
                    expandedId = -1;
                } else {
                    expandedId = item.id;
                }

                notifyDataSetChanged();
            });

            ui.pickBtn.setOnClickListener(v -> {
                FavoriteRouteUI item = getItem(getBindingAdapterPosition());
                listener.onPick(item);
            });

            ui.deleteBtn.setOnClickListener(v -> {
                FavoriteRouteUI item = getItem(getBindingAdapterPosition());
                listener.onDelete(item);
            });
        }

        void bind(FavoriteRouteUI route, boolean expanded) {
            ui.bind(route, expanded);
        }
    }
}
