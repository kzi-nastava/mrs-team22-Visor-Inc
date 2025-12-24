package inc.visor.voom.app.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    private final MutableLiveData<String> adress = new MutableLiveData<>();

    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    public ProfileViewModel() {
        firstName.setValue("Nikola");
        lastName.setValue("Bjelica");
        email.setValue("nikolabjelica4@gmail.com");
        adress.setValue("Tose Jovanovica 57a");
        phoneNumber.setValue("0600119031");
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

    public LiveData<String> getAdress() {
        return adress;
    }

    public LiveData<String> getPhoneNumeber() {
        return phoneNumber;
    }

    public void onSaveClicked(String fn, String ln, String em, String ad, String pn) {
        firstName.setValue(fn);
        lastName.setValue(ln);
        email.setValue(em);
        adress.setValue(ad);
        phoneNumber.setValue(pn);

    }

}
