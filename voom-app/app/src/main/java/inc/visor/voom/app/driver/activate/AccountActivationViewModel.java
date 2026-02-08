package inc.visor.voom.app.driver.activate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.driver.activate.repository.AccountActivationRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivationViewModel extends ViewModel {

    private final AccountActivationRepository repository = new AccountActivationRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void activate(String token, String password, String confirmPassword) {

        loading.setValue(true);

        Call<Void> call = repository.activate(token, password, confirmPassword);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setValue(false);

                if (response.isSuccessful()) {
                    success.setValue(true);
                } else {
                    error.setValue("Activation failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public LiveData<Boolean> isLoading() { return loading; }
    public LiveData<Boolean> isSuccess() { return success; }
    public LiveData<String> getError() { return error; }
}
