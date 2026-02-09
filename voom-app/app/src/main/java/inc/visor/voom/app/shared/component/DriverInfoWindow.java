package inc.visor.voom.app.shared.component;

import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import inc.visor.voom.app.R;
import inc.visor.voom.app.shared.model.SimulatedDriver;

public class DriverInfoWindow extends InfoWindow {

    public DriverInfoWindow(MapView mapView) {
        super(R.layout.driver_info_window, mapView);
    }

    @Override
    public void onOpen(Object item) {

        Marker marker = (Marker) item;
        SimulatedDriver driver =
                (SimulatedDriver) marker.getRelatedObject();

        TextView tvName = mView.findViewById(R.id.tv_driver_name);
        TextView tvStatus = mView.findViewById(R.id.tv_driver_status);

        if (driver == null) {
            tvName.setText("Suspended");
            tvStatus.setText("");
            return;
        }

        String first = driver.firstName != null ? driver.firstName : "";
        String last = driver.lastName != null ? driver.lastName : "";

        String fullName = (first + " " + last).trim();

        if (fullName.isEmpty()) {
            tvName.setText("Suspended");
        } else {
            tvName.setText(fullName);
        }

        tvStatus.setText(
                driver.status != null ? driver.status : ""
        );
    }


    @Override
    public void onClose() {
    }
}


