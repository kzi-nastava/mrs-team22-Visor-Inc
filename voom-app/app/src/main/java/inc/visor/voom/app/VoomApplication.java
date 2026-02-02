package inc.visor.voom.app;

import android.app.Application;
import inc.visor.voom.app.config.AppConfig;

public class VoomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
    }
}
