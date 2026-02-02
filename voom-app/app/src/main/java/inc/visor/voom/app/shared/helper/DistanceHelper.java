package inc.visor.voom.app.shared.helper;

public class DistanceHelper {

    public static float distanceInMeters(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(
                lat1, lon1,
                lat2, lon2,
                results
        );
        return results[0];
    }

}
