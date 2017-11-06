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
        SensorManager.registerListener((SensorEventListener) this, Accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /*Выполняется когда мы вызываем сервис посредством startSevice*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = new XYZAccelerometr(this);
        Accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        SensorManager.registerListener(acc, Accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setColor(Color.CYAN);
        startForeground(0x17, builder.build());
        //stopForeground(true);
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
