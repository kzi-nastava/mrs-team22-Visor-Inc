package inc.visor.voom.app.user.statistics.api;

import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportApi {
    @GET("api/reports")
    Call<ReportResponseDto> getReport(
            @Query("from") String from,
            @Query("to") String to,
            @Query("userId") Integer userId,
            @Query("driverId") Integer driverId
    );

}
