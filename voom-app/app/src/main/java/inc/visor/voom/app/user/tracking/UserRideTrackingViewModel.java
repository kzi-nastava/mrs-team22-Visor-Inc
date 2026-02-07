package inc.visor.voom.app.user.tracking;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.DriverPositionDto;
import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;
import inc.visor.voom.app.shared.dto.ride.RideResponseDto;
import inc.visor.voom.app.shared.repository.RouteRepository;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;
import inc.visor.voom.app.user.tracking.dto.RatingRequestDto;
import inc.visor.voom.app.user.tracking.dto.ComplaintRequestDto;
import inc.visor.voom.app.user.tracking.dto.RidePanicDto;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRideTrackingViewModel extends ViewModel implements DriverMetaProvider {

    private final DriverSimulationManager simulationManager = new DriverSimulationManager();

    public DriverSimulationManager getSimulationManager() {
        return simulationManager;
    }
    private final MutableLiveData<List<DriverSummaryDto>> activeDrivers = new MutableLiveData<>(new ArrayList<>());

    public void setActiveDrivers(List<DriverSummaryDto> list) {
    }

    private final MutableLiveData<Boolean> rideFinished = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> eta = new MutableLiveData<>(0);
    private final MutableLiveData<String> startAddress = new MutableLiveData<>("");
    private final MutableLiveData<String> destinationAddress = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> reported = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> reviewed = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> foundRide = new MutableLiveData<>(true);

    private final MutableLiveData<Boolean> panicked = new MutableLiveData<>(false);


    private final RouteRepository routeRepository = new RouteRepository();

    public LiveData<Boolean> hasPanicked() {
        return panicked;
    }

    private Long rideId;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    private Long driverId;
    private final RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);

    public LiveData<Boolean> isRideFinished() { return rideFinished; }
    public LiveData<Integer> getEta() { return eta; }
    public LiveData<String> getStartAddress() { return startAddress; }
    public LiveData<String> getDestinationAddress() { return destinationAddress; }
    public LiveData<Boolean> isReported() { return reported; }
    public LiveData<Boolean> isReviewed() { return reviewed; }

    public LiveData<Boolean> isRideFound() {
        return foundRide;
    }

    private final MutableLiveData<List<RoutePoint>> routePoints =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<RoutePoint>> getRoutePoints() {
        return routePoints;
    }


    public void initDrive() {
        simulationManager.startInterpolationLoop();

        rideApi.getOngoingRide().enqueue(new Callback<ActiveRideDto>() {
            @Override
            public void onResponse(Call<ActiveRideDto> call, Response<ActiveRideDto> response) {
                ActiveRideDto ride = response.body();
                if (ride == null || response.code() != 200) {
                    foundRide.setValue(false);
                    return;
                }

                rideId = ride.getRideId();
                driverId = ride.getDriverId();

                Log.d("RESTORE", "Restoring ride id: " + ride.rideId);

                List<RoutePoint> points = new ArrayList<>();

                for (RoutePointDto p : ride.routePoints) {

                    RoutePoint rp = new RoutePoint(
                            p.lat,
                            p.lng,
                            p.address,
                            p.orderIndex,
                            RoutePointDto.toPointType(p.type)
                    );

                    points.add(rp);
                }

                routePoints.setValue(points);

                startAddress.setValue(ride.getRoutePoints().get(0).address);
                destinationAddress.setValue(ride.getRoutePoints().get(ride.getRoutePoints().size()-1).address);
            }
            @Override
            public void onFailure(Call<ActiveRideDto> call, Throwable t) {
                foundRide.setValue(false);
            }
        });
    }



    public void updateRideStatus(DriverPositionDto pos) {
        if (driverId != null && pos.driverId == driverId) {
            if (pos.finished) {
                rideFinished.postValue(true);
            } else {
                eta.postValue((int) Math.ceil(pos.eta / 60.0));
            }
        }
    }

    public void submitReview(int driverRating, int carRating, String comment) {
        if (rideId == null) return;

        RatingRequestDto body = new RatingRequestDto();
        body.setComment(comment);
        body.setDriverRating(driverRating);
        body.setVehicleRating(carRating);

        rideApi.rateRide(rideId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) reviewed.setValue(true);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    @Override
    public DriverSummaryDto findActiveDriver(int id) {
        return null;
    }

    public void panic() {
        if (rideId == null) return;

        Disposable disposable = DataStoreManager.getInstance().getUserId().subscribe(userId -> {
            RidePanicDto body = new RidePanicDto();
            body.setUserId(userId);

            Log.d("USER_ID", "With user id: " + userId);

            Log.d("PANIC_DEBUG", "Sending panic request for ride: " + rideId);

            rideApi.panic(rideId, body).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        panicked.postValue(true);
                    } else {
                        Log.e("PANIC_API", "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("PANIC_API", "Network Failure", t);
                }
            });
        }, throwable -> {
            Log.e("PANIC_DEBUG", "Could not get User ID", throwable);
        });
    }

    public void reportRide(String message) {
        if (rideId == null) return;

        ComplaintRequestDto body = new ComplaintRequestDto();
        body.setMessage(message);

        rideApi.reportRide(rideId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) reported.setValue(true);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}