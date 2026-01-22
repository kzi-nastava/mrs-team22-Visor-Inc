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
        SimulatedDriver driver = (SimulatedDriver) marker.getRelatedObject();

        TextView name = mView.findViewById(R.id.tv_driver_name);
        TextView status = mView.findViewById(R.id.tv_driver_status);

        name.setText(driver.firstName + " " + driver.lastName);
        status.setText(driver.status);
    }

    @Override
    public void onClose() {}
}

