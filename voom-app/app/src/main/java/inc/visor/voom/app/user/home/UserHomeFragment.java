package inc.visor.voom.app.user.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import inc.visor.voom.app.R;
public class UserHomeFragment extends Fragment {

    private MapView mapView;
    private UserHomeViewModel viewModel;

    public UserHomeFragment() {
        super(R.layout.fragment_user_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserHomeViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        GeoPoint noviSad = new GeoPoint(45.2396, 19.8227);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(noviSad);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {
        });

        viewModel.isRideLocked().observe(getViewLifecycleOwner(), locked -> {
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
}
