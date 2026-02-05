package inc.visor.voom.app.shared;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import inc.visor.voom.app.shared.dto.authentication.TokenDto;
import io.reactivex.rxjava3.core.Single;

public class DataStoreManager {

    private static DataStoreManager instance;
    private final RxDataStore<Preferences> dataStore;

    private static final Preferences.Key<Long> USER_ID_KEY = PreferencesKeys.longKey("user_id");
    private static final Preferences.Key<String> USER_EMAIL_KEY = PreferencesKeys.stringKey("user_email");
    private static final Preferences.Key<String> USER_ROLE_KEY = PreferencesKeys.stringKey("user_role");
    private static final Preferences.Key<String> AUTH_TOKEN_KEY = PreferencesKeys.stringKey("auth_token");
    private static final Preferences.Key<Boolean> IS_LOGGED_IN_KEY = PreferencesKeys.booleanKey("is_logged_in");

    private DataStoreManager(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), "user_preferences").build();
    }

    public static synchronized DataStoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataStoreManager(context);
        }
        return instance;
    }

    public static synchronized DataStoreManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("DataStoreManager must be initialized in Application class");
        }
        return instance;
    }

    public void saveUserData(TokenDto dto) {
        dataStore.updateDataAsync(prefsIn -> {
            androidx.datastore.preferences.core.MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(USER_ID_KEY, dto.getUser().getId());
            mutablePreferences.set(USER_EMAIL_KEY, dto.getUser().getEmail());
            mutablePreferences.set(USER_ROLE_KEY, dto.getUser().getRole());
            mutablePreferences.set(AUTH_TOKEN_KEY, dto.getAccessToken());
            mutablePreferences.set(IS_LOGGED_IN_KEY, true);
            return Single.just(mutablePreferences);
        }).subscribe();
    }

    public Single<Long> getUserId() {
        return dataStore.data().map(prefs -> prefs.get(USER_ID_KEY))
            .firstOrError()
            .onErrorReturnItem(0L);
    }

    public Single<String> getUserEmail() {
        return dataStore.data().map(prefs -> prefs.get(USER_EMAIL_KEY))
            .firstOrError()
            .onErrorReturnItem("");
    }

    public Single<String> getUserRole() {
        return dataStore.data().map(prefs -> prefs.get(USER_ROLE_KEY))
            .firstOrError()
            .onErrorReturnItem("");
    }

    public Single<String> getAuthToken() {
        return dataStore.data().map(prefs -> prefs.get(AUTH_TOKEN_KEY))
            .firstOrError()
            .onErrorReturnItem("");
    }

    public Single<Boolean> isLoggedIn() {
        return dataStore.data().map(prefs -> {
            Boolean isLoggedIn = prefs.get(IS_LOGGED_IN_KEY);
            return isLoggedIn != null && isLoggedIn;
        }).firstOrError().onErrorReturnItem(false);
    }

    public void clearUserData() {
        dataStore.updateDataAsync(prefsIn -> {
            androidx.datastore.preferences.core.MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.clear();
            return Single.just(mutablePreferences);
        }).subscribe();
    }
}
