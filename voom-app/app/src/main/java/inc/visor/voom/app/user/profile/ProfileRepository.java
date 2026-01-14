package inc.visor.voom.app.user.profile;

import android.util.Log;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.user.profile.dto.UserProfileDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private final ProfileApi api;

    public ProfileRepository() {
        api = RetrofitClient
                .getInstance()
                .create(ProfileApi.class);
    }

    public void getProfile(ProfileCallback callback) {

        Log.d("PROFILE_API", "GET /api/users/me REQUEST SENT");

        api.getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<UserProfileDto> call,
                    Response<UserProfileDto> response
            ) {
                Log.d("PROFILE_API", "RESPONSE CODE = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PROFILE_API", "RESPONSE BODY OK");
                    callback.onSuccess(response.body());
                } else {
                    Log.e("PROFILE_API", "RESPONSE FAILED");
                    callback.onError("Profile fetch failed");
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                Log.e("PROFILE_API", "REQUEST FAILED", t);
                callback.onError(t.getMessage());
            }
        });
    }


    public interface ProfileCallback {
        void onSuccess(UserProfileDto dto);
        void onError(String error);
    }
}
