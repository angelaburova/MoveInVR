package angelaburova.com.moveinvr;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class ServiceAcc extends Service {
    private android.hardware.SensorManager SensorManager;
    private Sensor Accelerometer;
    private Sensor counterStep;
    private Sensor gravity;
    private XYZAccelerometr acc;


    public ServiceAcc() {

    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    public void onPause() {
        SensorManager.unregisterListener((SensorEventListener) this);
    }

    public void onResume() {
        SensorManager.registerListener((SensorEventListener) this, Accelerometer, SensorManager.SENSOR_DELAY_GAME);
        SensorManager.registerListener((SensorEventListener) this, counterStep, SensorManager.SENSOR_DELAY_GAME);
        SensorManager.registerListener((SensorEventListener) this, gravity, SensorManager.SENSOR_DELAY_GAME);
    }

    /*Выполняется когда мы вызываем сервис посредством startSevice*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = new XYZAccelerometr(this);
        Accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        counterStep=SensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        gravity=SensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        SensorManager.registerListener(acc, Accelerometer, SensorManager.SENSOR_DELAY_GAME);
        SensorManager.registerListener(acc, counterStep, SensorManager.SENSOR_DELAY_GAME);
        SensorManager.registerListener(acc, gravity, SensorManager.SENSOR_DELAY_GAME);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setColor(Color.CYAN);
        startForeground(0x17, builder.build());
        stopForeground(true);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
