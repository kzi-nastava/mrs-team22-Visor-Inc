package inc.visor.voom.app.unauthenticated.registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;

public class RegistrationViewModel extends ViewModel {

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> birthDate = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> repeatPassword = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationComplete = new MutableLiveData<>();
    private final MutableLiveData<Boolean> redirectToLogin = new MutableLiveData<>(false);

    public void setFirstName(String value) {
        firstName.setValue(value);
    }

    public LiveData<String> getFirstName() {
        return firstName;
    }

    public void setLastName(String value) {
        lastName.setValue(value);
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public void setBirthDate(LocalDateTime date) {
        birthDate.setValue(date);
    }

    public LiveData<LocalDateTime> getBirthDate() {
        return birthDate;
    }

    public void setEmail(String value) {
        email.setValue(value);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setPassword(String value) {
        password.setValue(value);
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public void setRepeatPassword(String value) {
        repeatPassword.setValue(value);
    }

    public LiveData<String> getRepeatPassword() {
        return repeatPassword;
    }

    public void setAddress(String value) {
        address.setValue(value);
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public void setPhoneNumber(String value) {
        phoneNumber.setValue(value);
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setRegistrationComplete(boolean registrationComplete) {
        this.registrationComplete.setValue(registrationComplete);
    }

    public MutableLiveData<Boolean> getRegistrationComplete() {
        return registrationComplete;
    }

    public void setRedirectToLogin() {
        this.redirectToLogin.setValue(true);
    }

    public MutableLiveData<Boolean> getRedirectToLogin() {
        return redirectToLogin;
    }
}
