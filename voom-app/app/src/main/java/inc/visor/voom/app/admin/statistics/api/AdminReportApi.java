package inc.visor.voom.app.admin.statistics.api;

import inc.visor.voom.app.admin.statistics.dto.ReportResponseDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AdminReportApi {

    @GET("api/reports")
    Call<ReportResponseDto> getReport(
            @Query("from") String from,
            @Query("to") String to,
            @Query("userId") Long userId,
            @Query("driverId") Long driverId
    );
}
