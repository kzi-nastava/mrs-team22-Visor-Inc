package inc.visor.voom.app.admin.pricing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import inc.visor.voom.app.admin.pricing.api.VehicleTypeApi;
import inc.visor.voom.app.admin.pricing.dto.VehicleTypeDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPricingViewModel extends ViewModel {

    private final MutableLiveData<List<VehicleTypeDto>> vehicleTypes = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private VehicleTypeApi api;

    public EditPricingViewModel() {
        api = RetrofitClient.getInstance().create(VehicleTypeApi.class);
    }

    public LiveData<List<VehicleTypeDto>> getVehicleTypesData() {
        return vehicleTypes;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void fetchVehicleTypes() {
        api.getVehicleTypes().enqueue(new Callback<List<VehicleTypeDto>>() {
            @Override
            public void onResponse(Call<List<VehicleTypeDto>> call, Response<List<VehicleTypeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleTypes.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VehicleTypeDto>> call, Throwable t) {
                statusMessage.setValue("Failed to fetch data: " + t.getMessage());
            }
        });
    }

    public void updatePrice(long id, String typeName, Double newPrice) {
        VehicleTypeDto dto = new VehicleTypeDto(id, typeName, newPrice);
        api.updateVehicleType(id, dto).enqueue(new Callback<VehicleTypeDto>() {
            @Override
            public void onResponse(Call<VehicleTypeDto> call, Response<VehicleTypeDto> response) {
                if (response.isSuccessful()) {
                    List<VehicleTypeDto> current = vehicleTypes.getValue();

                    if (current != null) {
                        for (VehicleTypeDto item : current) {
                            if (item.getId() == id) {
                                item.setPrice(newPrice);
                                break;
                            }
                        }
                        vehicleTypes.setValue(current);
                    }
                    statusMessage.setValue("Price updated");
                } else {
                    statusMessage.setValue("Update failed");
                }
            }

            @Override
            public void onFailure(Call<VehicleTypeDto> call, Throwable t) {
                statusMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}