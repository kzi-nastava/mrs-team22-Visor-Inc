package inc.visor.voom.app.driver.activate.repository;

import inc.visor.voom.app.driver.activate.api.DriverActivationApi;
import inc.visor.voom.app.driver.activate.dto.ActivateDriverRequestDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;

public class AccountActivationRepository {

    private final DriverActivationApi api =
            RetrofitClient.getInstance().create(DriverActivationApi.class);

    public Call<Void> activate(String token, String password, String confirmPassword) {
        return api.activate(new ActivateDriverRequestDto(token, password, confirmPassword));
    }
}
