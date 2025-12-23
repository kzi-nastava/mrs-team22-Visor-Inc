package inc.visor.voom.app.unauthenticated.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

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
}
