package angelaburova.com.walkinvr;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class ServiceAcc extends Service {
    private android.hardware.SensorManager SensorManager;
    private Sensor accelerometerLinear;
    private Sensor accelerometer;
    private Sensor step;
    private Sensor gravity;
    private Sensor magnetic;
    private ReadSensors acc;
    private String TAG="myLog";
    private boolean calibration;




    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void onPause() {
        SensorManager.unregisterListener((SensorEventListener) this);

    }

    public void onResume() {

        SensorManager.registerListener(acc, accelerometerLinear, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, gravity, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, magnetic, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc,step, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"start service");
        calibration = intent.getBooleanExtra("calibration",false);
        SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try {
            acc = new ReadSensors(this,calibration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        accelerometerLinear = SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gravity=SensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        step=SensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorManager.registerListener(acc, accelerometerLinear, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, gravity, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, magnetic, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(acc, step, SensorManager.SENSOR_DELAY_FASTEST);
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
