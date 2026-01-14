package inc.visor.voom.app.user.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.user.profile.dto.UpdateUserProfileRequestDto;
import inc.visor.voom.app.user.profile.dto.UserProfileDto;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository repository = new ProfileRepository();

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    public void loadProfile() {
        repository.getProfile(new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileDto dto) {
                firstName.postValue(dto.getFirstName());
                lastName.postValue(dto.getLastName());
                email.postValue(dto.getEmail());
                address.postValue(dto.getAddress());
                phoneNumber.postValue(dto.getPhoneNumber());
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
            }

            @Override
            public void onError(String error) {
            }
        });
    }


    public LiveData<String> getFirstName() {
        return firstName;
    }

    public LiveData<String> getLastName() {
        return lastName;
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

    public void onSaveClicked(String fn, String ln, String em, String ad, String pn) {
        firstName.setValue(fn);
        lastName.setValue(ln);
        email.setValue(em);
        address.setValue(ad);
        phoneNumber.setValue(pn);
        saveProfile(fn, ln, pn, ad);
    }
}
