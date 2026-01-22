package inc.visor.voom.app.shared.simulation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.shared.model.FreeDriverSnapshot;
import inc.visor.voom.app.shared.model.SimulatedDriver;

public class DriverSimulationManager {

    private final List<SimulatedDriver> drivers = new ArrayList<>();

    private final MutableLiveData<List<SimulatedDriver>> driversLive =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<SimulatedDriver>> getDrivers() {
        return driversLive;
    }

    public void updateDriverPosition(long driverId, double lat, double lng) {

        SimulatedDriver driver = findDriver(driverId);

        GeoPoint target = new GeoPoint(lat, lng);

        if (driver == null) {
            driver = new SimulatedDriver();
            driver.id = driverId;
            driver.currentPosition = target;
            drivers.add(driver);
        } else {
            driver.lastPosition = driver.currentPosition;
            driver.targetPosition = target;
            driver.animStart = System.currentTimeMillis();
        }

        driversLive.postValue(drivers);
    }

    private SimulatedDriver findDriver(long id) {
        for (SimulatedDriver d : drivers)
            if (d.id == id) return d;
        return null;
    }

    public List<FreeDriverSnapshot> getFreeDriversSnapshot() {

        List<FreeDriverSnapshot> snapshot = new ArrayList<>();

        for (SimulatedDriver d : drivers) {
            if ("FREE".equals(d.status)) {

                snapshot.add(new FreeDriverSnapshot(
                        d.id,
                        d.currentPosition.getLatitude(),
                        d.currentPosition.getLongitude()
                ));
            }
        }

        return snapshot;
    }

    public void startInterpolationLoop() {

        new Thread(() -> {
            while (true) {

                long now = System.currentTimeMillis();

                for (SimulatedDriver d : drivers) {

                    if (d.targetPosition == null || d.lastPosition == null)
                        continue;

                    float t = (now - d.animStart) / (float) d.animDuration;

                    if (t >= 1f) {
                        d.currentPosition = d.targetPosition;
                        d.targetPosition = null;
                    } else {

                        double lat = d.lastPosition.getLatitude()
                                + (d.targetPosition.getLatitude()
                                - d.lastPosition.getLatitude()) * t;

                        double lng = d.lastPosition.getLongitude()
                                + (d.targetPosition.getLongitude()
                                - d.lastPosition.getLongitude()) * t;

                        d.currentPosition = new GeoPoint(lat, lng);
                    }
                }

                driversLive.postValue(new ArrayList<>(drivers));

                try { Thread.sleep(16); }
                catch (InterruptedException ignored) {}
            }
        }).start();
    }

}

