package inc.visor.voom.app.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    public ProfileViewModel() {
        firstName.setValue("Nikola");
        lastName.setValue("Bjelica");
        email.setValue("nikolabjelica4@gmail.com");
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

    public void onSaveClicked(String fn, String ln, String em) {
        firstName.setValue(fn);
        lastName.setValue(ln);
        email.setValue(em);
    }

}
