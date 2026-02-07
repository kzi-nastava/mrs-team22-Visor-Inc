package inc.visor.voom.app.user.statistics.repository;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.user.statistics.api.ReportApi;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import retrofit2.Callback;

public class UserStatisticsRepository {

    private final ReportApi api;

    public UserStatisticsRepository() {
        api = RetrofitClient
                .getInstance()
                .create(ReportApi.class);
    }

    public void getReport(
            String from,
            String to,
            Integer userId,
            Callback<ReportResponseDto> callback
    ) {
        api.getReport(from, to, userId, null)
                .enqueue(callback);
    }
}

