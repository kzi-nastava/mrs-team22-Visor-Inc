package inc.visor.voom.app.driver.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.driver.profile.dto.VehicleSummaryDto;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.user.profile.ProfileRepository;
import inc.visor.voom.app.user.profile.dto.UpdateUserProfileRequestDto;
import inc.visor.voom.app.user.profile.dto.UserProfileDto;

public class DriverProfileViewModel extends ViewModel {

    private final ProfileRepository repository = new ProfileRepository();

    private final VehicleRepository vehicleRepository = new VehicleRepository();

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private final MutableLiveData<Boolean> profileUpdated = new MutableLiveData<>();

    private final MutableLiveData<String> fullName = new MutableLiveData<>();

    private final MutableLiveData<String> vehicleModel = new MutableLiveData<>();
    private final MutableLiveData<String> vehicleType = new MutableLiveData<>();
    private final MutableLiveData<String> licensePlate = new MutableLiveData<>();
    private final MutableLiveData<Integer> numberOfSeats = new MutableLiveData<>();
    private final MutableLiveData<Boolean> babyTransportAllowed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> petTransportAllowed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> vehicleUpdated = new MutableLiveData<>();


    public void loadProfile() {
        repository.getProfile(new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileDto dto) {
                firstName.postValue(dto.getFirstName());
                lastName.postValue(dto.getLastName());
                email.postValue(dto.getEmail());
                address.postValue(dto.getAddress());
                phoneNumber.postValue(dto.getPhoneNumber());
                fullName.postValue(dto.getFirstName() + " " + dto.getLastName());
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public void loadVehicle() {
        vehicleRepository.getVehicle(new VehicleRepository.CallbackApi() {
            @Override
            public void onSuccess(VehicleSummaryDto dto) {
                vehicleModel.postValue(dto.getModel());
                vehicleType.postValue(dto.getVehicleType());
                licensePlate.postValue(dto.getLicensePlate());
                numberOfSeats.postValue(dto.getNumberOfSeats());
                babyTransportAllowed.postValue(dto.isBabySeat());
                petTransportAllowed.postValue(dto.isPetFriendly());
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public void saveVehicle(
            String model,
            String type,
            String plate,
            Integer seats,
            boolean baby,
            boolean pet
    ) {

        VehicleSummaryDto body = new VehicleSummaryDto();
        body.setModel(model);
        body.setVehicleType(type);
        body.setLicensePlate(plate);
        body.setNumberOfSeats(seats != null ? seats : 0);
        body.setBabySeat(baby);
        body.setPetFriendly(pet);

        vehicleRepository.updateVehicle(body, new VehicleRepository.CallbackApi() {
            @Override
            public void onSuccess(VehicleSummaryDto dto) {
                vehicleUpdated.postValue(true);
                loadVehicle();
            }

            @Override
            public void onError(String error) {
            }
        });
    }


    public void saveProfile(
            String fn,
            String ln,
            String pn,
            String ad
    ) {
        UpdateUserProfileRequestDto body =
                new UpdateUserProfileRequestDto(fn, ln, pn, ad);

        repository.updateProfile(body, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileDto dto) {
                firstName.postValue(dto.getFirstName());
                lastName.postValue(dto.getLastName());
                phoneNumber.postValue(dto.getPhoneNumber());
                address.postValue(dto.getAddress());
                profileUpdated.postValue(true);
                loadProfile();
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public void onSaveClicked(
            String fn,
            String ln,
            String em,
            String ad,
            String pn,
            String vehicleModel,
            String vehicleType,
            String licensePlate,
            Integer numberOfSeats,
            boolean babyTransportAllowed,
            boolean petTransportAllowed
    ) {
        firstName.setValue(fn);
        lastName.setValue(ln);
        email.setValue(em);
        address.setValue(ad);
        phoneNumber.setValue(pn);

        this.vehicleModel.setValue(vehicleModel);
        this.vehicleType.setValue(vehicleType);
        this.licensePlate.setValue(licensePlate);
        this.numberOfSeats.setValue(numberOfSeats);
        this.babyTransportAllowed.setValue(babyTransportAllowed);
        this.petTransportAllowed.setValue(petTransportAllowed);

        saveProfile(fn, ln, pn, ad);
    }

    public LiveData<String> getFirstName() {
        return firstName;
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public LiveData<String> getFullName() {
        return fullName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<Boolean> getProfileUpdated() {
        return profileUpdated;
    }

    public LiveData<String> getVehicleModel() {
        return vehicleModel;
    }

    public LiveData<String> getVehicleType() {
        return vehicleType;
    }

    public LiveData<String> getLicensePlate() {
        return licensePlate;
    }

    public LiveData<Integer> getNumberOfSeats() {
        return numberOfSeats;
    }

    public LiveData<Boolean> isBabyTransportAllowed() {
        return babyTransportAllowed;
    }

    public LiveData<Boolean> isPetTransportAllowed() {
        return petTransportAllowed;
    }

    public LiveData<Boolean> getVehicleUpdated() {
        return vehicleUpdated;
    }
}
