package inc.visor.voom.app.driver.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.driver.create.dto.CreateDriverRequestDto;
import inc.visor.voom.app.driver.create.dto.CreateVehicleRequestDto;
import inc.visor.voom.app.driver.create.repository.CreateDriverRepository;

public class CreateDriverViewModel extends ViewModel {

    private final CreateDriverRepository repository = new CreateDriverRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> driverCreated = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void createDriver(
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String address,
            String vehicleModel,
            String vehicleType,
            String licensePlate,
            Integer numberOfSeats,
            boolean babyTransportAllowed,
            boolean petTransportAllowed
    ) {

        if (email == null || email.isBlank()
                || firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()
                || phoneNumber == null || phoneNumber.isBlank()
                || address == null || address.isBlank()
                || vehicleModel == null || vehicleModel.isBlank()
                || vehicleType == null || vehicleType.isBlank()
                || licensePlate == null || licensePlate.isBlank()
        ) {
            error.postValue("All fields are required");
            return;
        }

        loading.postValue(true);

        CreateVehicleRequestDto vehicle =
                new CreateVehicleRequestDto(
                        vehicleModel,
                        vehicleType,
                        licensePlate,
                        numberOfSeats != null ? numberOfSeats : 0,
                        babyTransportAllowed,
                        petTransportAllowed
                );

        CreateDriverRequestDto body =
                new CreateDriverRequestDto(
                        email,
                        firstName,
                        lastName,
                        phoneNumber,
                        address,
                        vehicle
                );
        repository.createDriver(body, new CreateDriverRepository.CallbackApi() {
            @Override
            public void onSuccess() {
                loading.postValue(false);
                driverCreated.postValue(true);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }
    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isDriverCreated() {
        return driverCreated;
    }

    public LiveData<String> getError() {
        return error;
    }
}
