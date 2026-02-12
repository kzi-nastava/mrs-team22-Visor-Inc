package inc.visor.voom.app.shared.component.history;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    // Shake detection parameters
    private static final float SHAKE_THRESHOLD = 2.7f; // m/s^2
    private static final int SHAKE_TIME_LAPSE = 500; // ms between shakes

    private OnShakeListener listener;
    private long lastShakeTime;

    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (listener == null) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD) {
            long currentTime = System.currentTimeMillis();

            if (lastShakeTime + SHAKE_TIME_LAPSE > currentTime) {
                return;
            }

            lastShakeTime = currentTime;
            listener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}