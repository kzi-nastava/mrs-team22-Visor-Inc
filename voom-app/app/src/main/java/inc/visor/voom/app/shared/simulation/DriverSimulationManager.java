package inc.visor.voom.app.shared.simulation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.shared.model.DriverLocationDto;
import inc.visor.voom.app.shared.model.SimulatedDriver;

public class DriverSimulationManager {

    private final List<SimulatedDriver> drivers = new ArrayList<>();

    private final MutableLiveData<List<SimulatedDriver>> driversLive =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<SimulatedDriver>> getDrivers() {
        return driversLive;
    }

    public void updateDriverPosition(long driverId,
                                     double lat,
                                     double lng,
                                     DriverSummaryDto meta) {

        SimulatedDriver driver = findDriver(driverId);
        GeoPoint target = new GeoPoint(lat, lng);

        if (driver == null) {
            driver = new SimulatedDriver();
            driver.id = driverId;
            driver.currentPosition = target;

            if (meta != null) {
                driver.firstName = meta.firstName;
                driver.lastName = meta.lastName;
                driver.status = meta.status;
            }

            drivers.add(driver);
        } else {
            driver.lastPosition = driver.currentPosition;
            driver.targetPosition = target;
            driver.animStart = System.currentTimeMillis();

            if (meta != null) {
                if (meta.firstName != null) driver.firstName = meta.firstName;
                if (meta.lastName != null) driver.lastName = meta.lastName;
                if (meta.status != null) driver.status = meta.status;
            }
        }

        driversLive.postValue(new ArrayList<>(drivers));
    }

    private SimulatedDriver findDriver(long id) {
        for (SimulatedDriver d : drivers)
            if (d.id == id) return d;
        return null;
    }

    public List<DriverLocationDto> getFreeDriversSnapshot() {

        List<DriverLocationDto> snapshot = new ArrayList<>();

        for (SimulatedDriver d : drivers) {

                DriverLocationDto location = new DriverLocationDto(
                        d.id,
                        d.currentPosition.getLatitude(),
                        d.currentPosition.getLongitude()
                );
                Log.d("LOCATION ", location.toString());
                snapshot.add(location);
            
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

