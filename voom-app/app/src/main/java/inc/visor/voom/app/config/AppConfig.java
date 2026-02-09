package inc.visor.voom.app.config;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {

    private static final String PREF_NAME = "voom_config";
    private static final String KEY_SERVER_IP = "server_ip";

    private static SharedPreferences preferences;

    private static final String DEFAULT_IP = "192.168.1.10";
    private static final String PORT = "8080";

    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setServerIp(String ip) {
        preferences.edit().putString(KEY_SERVER_IP, ip).apply();
    }

    public static String getServerIp() {
        return preferences.getString(KEY_SERVER_IP, DEFAULT_IP);
    }

    public static String getBaseUrl() {
        return "http://" + getServerIp() + ":" + PORT + "/";
    }

    public static String getWsUrl() {
        return "ws://" + getServerIp() + ":" + PORT + "/ws";
    }
}

