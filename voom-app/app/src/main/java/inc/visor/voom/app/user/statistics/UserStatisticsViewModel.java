package inc.visor.voom.app.user.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import inc.visor.voom.app.user.statistics.repository.UserStatisticsRepository;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
public class UserStatisticsViewModel extends ViewModel {

    private final UserStatisticsRepository repository =
            new UserStatisticsRepository();

    private final MutableLiveData<ReportResponseDto> report =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    public LiveData<ReportResponseDto> getReport() {
        return report;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void loadReport(String from, String to, Integer userId) {

        loading.setValue(true);

        repository.getReport(from, to, userId,
                new Callback<ReportResponseDto>() {
                    @Override
                    public void onResponse(
                            Call<ReportResponseDto> call,
                            Response<ReportResponseDto> response
                    ) {
                        loading.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            report.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReportResponseDto> call, Throwable t) {
                        loading.postValue(false);
                    }
                });
    }
}

