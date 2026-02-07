package inc.visor.voom.app.driver.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.driver.statistics.repository.DriverStatisticsRepository;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;


public class DriverStatisticsViewModel extends ViewModel {

    private final DriverStatisticsRepository repository =
            new DriverStatisticsRepository();

    private final MutableLiveData<ReportResponseDto> report =
            new MutableLiveData<>();

    public LiveData<ReportResponseDto> getReport() {
        return report;
    }

    public void loadReport(String from, String to, Integer driverId) {

        repository.getReport(from, to, driverId,
                new Callback<ReportResponseDto>() {
                    @Override
                    public void onResponse(
                            Call<ReportResponseDto> call,
                            Response<ReportResponseDto> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            report.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReportResponseDto> call, Throwable t) {
                    }
                });
    }
}
