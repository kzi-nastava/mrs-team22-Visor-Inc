package inc.visor.voom.app.shared;

import com.auth0.android.jwt.JWT;

import java.util.Date;

import inc.visor.voom.app.shared.api.AuthenticationApi;
import inc.visor.voom.app.shared.dto.authentication.TokenDto;
import io.reactivex.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationService {

    AuthenticationApi authenticationApi;
    DataStoreManager dataStoreManager;
    private String refreshToken;
    private TokenDto tokenDto;

    public AuthenticationService(AuthenticationApi authenticationApi, DataStoreManager dataStoreManager) {
        this.authenticationApi = authenticationApi;
        this.dataStoreManager = dataStoreManager;
    }

    private boolean isValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            JWT jwt = new JWT(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt == null) {
                return false;
            }
            return expiresAt.getTime() >= System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

//    public Observable<String> getAccessToken() {
//        if (tokenDto != null && isValid(tokenDto.getAccessToken())) {
//            return Observable.just(tokenDto.getAccessToken());
//        } else {
//            return authenticationApi.refreshToken(refreshToken != null ? refreshToken : "").enqueue(new Callback<TokenDto>() {
//                @Override
//                public void onResponse(Call<TokenDto> call, Response<TokenDto> response) {
//
//                }
//
//                @Override
//                public void onFailure(Call<TokenDto> call, Throwable t) {
//
//                }
//            });
//        }
//    }

}