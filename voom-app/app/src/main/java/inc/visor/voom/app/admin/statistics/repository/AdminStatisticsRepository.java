package inc.visor.voom.app.admin.statistics.repository;

import inc.visor.voom.app.admin.statistics.api.AdminReportApi;
import inc.visor.voom.app.admin.statistics.dto.ReportResponseDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Callback;

public class AdminStatisticsRepository {

    private final AdminReportApi api;

    public AdminStatisticsRepository() {
        api = RetrofitClient
                .getInstance()
                .create(AdminReportApi.class);
    }

    public void getReport(
            String from,
            String to,
            Long userId,
            Long driverId,
            Callback<ReportResponseDto> callback
    ) {
        api.getReport(from, to, userId, driverId)
                .enqueue(callback);
    }
}
