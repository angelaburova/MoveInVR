package angelaburova.com.moveinvr;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


public abstract class Accelerometr  implements SensorEventListener{
    protected double lastX,lastY,lastZ;
    @Override
    public abstract void onSensorChanged(SensorEvent event);
    @Override
    public abstract void onAccuracyChanged(Sensor sensor, int accuracy);
}
