package inc.visor.voom.app.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.user.profile.dto.ChangePasswordRequestDto;

public class ChangePasswordViewModel extends ViewModel {


    private final ProfileRepository repository = new ProfileRepository();

    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void changePassword(String newPassword, String confirmPassword) {
        ChangePasswordRequestDto body =
                new ChangePasswordRequestDto(newPassword, confirmPassword);

        repository.changePassword(body, new ProfileRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                success.postValue(true);
            }

            @Override
            public void onError(String err) {
                error.postValue(err);
            }
        });
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }
}
