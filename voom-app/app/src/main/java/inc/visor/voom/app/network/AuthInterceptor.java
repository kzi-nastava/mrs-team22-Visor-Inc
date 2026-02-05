package inc.visor.voom.app.network;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import inc.visor.voom.app.shared.DataStoreManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        String token = DataStoreManager.getInstance().getAuthToken().blockingGet();

        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}
