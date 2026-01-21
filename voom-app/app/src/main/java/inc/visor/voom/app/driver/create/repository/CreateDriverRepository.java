package inc.visor.voom.app.driver.create.repository;

import inc.visor.voom.app.driver.create.dto.CreateDriverRequestDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDriverRepository {

    private final CreateDriverApi api;

    public CreateDriverRepository() {
        api = RetrofitClient
                .getInstance()
                .create(CreateDriverApi.class);
    }

    public void createDriver(
            CreateDriverRequestDto body,
            CallbackApi callback
    ) {

        api.createDriver(body).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(
                            "Create driver failed (" + response.code() + ")"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {
                callback.onError(
                        t.getMessage() != null
                                ? t.getMessage()
                                : "Network error"
                );
            }
        });
    }

    public interface CallbackApi {
        void onSuccess();
        void onError(String message);
    }
}
