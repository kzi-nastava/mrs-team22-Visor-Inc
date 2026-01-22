package inc.visor.voom.app.shared.repository;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

public class LocationRepository {

    private final Context context;

    public LocationRepository(Context context) {
        this.context = context;
    }

    public String getAddress(double lat, double lng) {

        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses =
                    geocoder.getFromLocation(lat, lng, 1);

            if (!addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}

