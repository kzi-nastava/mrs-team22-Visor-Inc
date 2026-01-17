package inc.visor.voom.app.user.profile;

import android.media.MediaRouter;
import android.util.Log;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.user.profile.dto.ChangePasswordRequestDto;
import inc.visor.voom.app.user.profile.dto.UpdateUserProfileRequestDto;
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

        api.getProfile().enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(
                    Call<UserProfileDto> call,
                    Response<UserProfileDto> response
            ) {
                Log.d("PROFILE_API", "RESPONSE CODE = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Profile fetch failed");
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateProfile(
            UpdateUserProfileRequestDto body,
            ProfileCallback callback
    ) {

        Log.d("PROFILE_API", "PUT /api/users/me REQUEST SENT");

        api.updateProfile(body).enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(
                    Call<UserProfileDto> call,
                    Response<UserProfileDto> response
            ) {
                Log.d("PROFILE_API", "RESPONSE CODE = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Profile update failed");
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void changePassword(
            ChangePasswordRequestDto body,
            SimpleCallback callback
    ) {
        Log.d("PROFILE_API", "PUT /api/users/password REQUEST SENT");

        api.changePassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("PROFILE_API", "RESPONSE CODE = " + response.code());

                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Password change failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ProfileCallback {
        void onSuccess(UserProfileDto dto);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }
}
