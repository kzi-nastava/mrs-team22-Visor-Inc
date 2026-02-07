package inc.visor.voom.app.driver.statistics.repository;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.user.statistics.api.ReportApi;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import retrofit2.Callback;

public class DriverStatisticsRepository {

    private final ReportApi api;

    public DriverStatisticsRepository() {
        api = RetrofitClient
                .getInstance()
                .create(ReportApi.class);
    }

    public void getReport(
            String from,
            String to,
            Integer driverId,
            Callback<ReportResponseDto> callback
    ) {
        api.getReport(from, to, null, driverId)
                .enqueue(callback);
    }
}

