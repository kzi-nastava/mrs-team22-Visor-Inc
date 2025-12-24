package inc.visor.voom.app.driver.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DriverProfileViewModel extends ViewModel {

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    private final MutableLiveData<String> vehicleModel = new MutableLiveData<>();
    private final MutableLiveData<String> vehicleType = new MutableLiveData<>();
    private final MutableLiveData<String> licensePlate = new MutableLiveData<>();
    private final MutableLiveData<Integer> numberOfSeats = new MutableLiveData<>();

    private final MutableLiveData<Boolean> babyTransportAllowed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> petTransportAllowed = new MutableLiveData<>(false);

    public LiveData<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName.setValue(value);
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        lastName.setValue(value);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email.setValue(value);
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public void setAddress(String value) {
        address.setValue(value);
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        phoneNumber.setValue(value);
    }

    public LiveData<String> getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String value) {
        vehicleModel.setValue(value);
    }

    public LiveData<String> getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String value) {
        vehicleType.setValue(value);
    }

    public LiveData<String> getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String value) {
        licensePlate.setValue(value);
    }

    public LiveData<Integer> getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer value) {
        numberOfSeats.setValue(value);
    }

    public LiveData<Boolean> isBabyTransportAllowed() {
        return babyTransportAllowed;
    }

    public void setBabyTransportAllowed(Boolean value) {
        babyTransportAllowed.setValue(value);
    }

    public LiveData<Boolean> isPetTransportAllowed() {
        return petTransportAllowed;
    }

    public void setPetTransportAllowed(Boolean value) {
        petTransportAllowed.setValue(value);
    }

    public void onSaveClicked(
            String firstName,
            String lastName,
            String email,
            String address,
            String phoneNumber,
            String vehicleModel,
            String vehicleType,
            String licensePlate,
            Integer numberOfSeats,
            boolean babyTransportAllowed,
            boolean petTransportAllowed
    ) {
        this.firstName.setValue(firstName);
        this.lastName.setValue(lastName);
        this.email.setValue(email);
        this.address.setValue(address);
        this.phoneNumber.setValue(phoneNumber);

        this.vehicleModel.setValue(vehicleModel);
        this.vehicleType.setValue(vehicleType);
        this.licensePlate.setValue(licensePlate);
        this.numberOfSeats.setValue(numberOfSeats);

        this.babyTransportAllowed.setValue(babyTransportAllowed);
        this.petTransportAllowed.setValue(petTransportAllowed);

    }

}
