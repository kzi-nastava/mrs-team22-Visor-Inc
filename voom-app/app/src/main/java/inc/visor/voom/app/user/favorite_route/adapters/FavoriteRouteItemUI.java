package inc.visor.voom.app.user.favorite_route.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteUI;

public class FavoriteRouteItemUI {

    private final View expandContent;
    private final View divider;
    private final ImageView arrow;

    private final TextView name;
    private final TextView distance;
    private final TextView startEnd;
    private final TextView stops;

    public final MaterialButton pickBtn;
    public final MaterialButton deleteBtn;

    public FavoriteRouteItemUI(View root) {

        name = root.findViewById(R.id.name);
        distance = root.findViewById(R.id.distance);
        startEnd = root.findViewById(R.id.startEnd);
        stops = root.findViewById(R.id.stops);

        expandContent = root.findViewById(R.id.expandContent);
        divider = root.findViewById(R.id.divider);
        arrow = root.findViewById(R.id.arrow);

        pickBtn = root.findViewById(R.id.pickBtn);
        deleteBtn = root.findViewById(R.id.deleteBtn);
    }

    public void bind(FavoriteRouteUI route, boolean expanded) {

        name.setText(route.name);
        distance.setText("~" + route.distanceKm + " km");
        startEnd.setText(route.start + " → " + route.end);

        if (route.stops.isEmpty()) {
            stops.setText("No intermediate stops");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : route.stops) {
                sb.append("• ").append(s).append("\n");
            }
            stops.setText(sb.toString());
        }

        expandContent.setVisibility(expanded ? View.VISIBLE : View.GONE);
        divider.setVisibility(expanded ? View.VISIBLE : View.GONE);
        arrow.setRotation(expanded ? 180f : 0f);
    }
}
