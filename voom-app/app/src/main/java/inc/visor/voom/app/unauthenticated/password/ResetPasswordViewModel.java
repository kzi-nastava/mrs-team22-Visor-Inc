package inc.visor.voom.app.unauthenticated.password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResetPasswordViewModel extends ViewModel {

    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> repeatPassword = new MutableLiveData<>();

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

}
