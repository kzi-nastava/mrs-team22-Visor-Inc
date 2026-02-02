package inc.visor.voom.app.shared.model;

import org.osmdroid.util.GeoPoint;

public class SimulatedDriver {

    public long id;
    public String firstName;
    public String lastName;
    public String status;
    public GeoPoint currentPosition;
    public GeoPoint lastPosition;
    public GeoPoint targetPosition;

    public long animStart;
    public long animDuration = 3000;
}

