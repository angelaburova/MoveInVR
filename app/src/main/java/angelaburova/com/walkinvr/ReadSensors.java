package angelaburova.com.walkinvr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ReadSensors implements SensorEventListener {

    //Constants for calibration and reading sensors
    private static final int BUFFER = 50;
    private static final int BUFFERCLBR = 300;
    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //Points for storage data
    private Point<Float> currentPoint;
    private Point<Float> sumPoint;
    public Point<Float> avgPoint;
    private Point<Float> distance;
    private Point<Float> speed;
    private Point<Float> dPoint;
    private Point<Float> gravity;
    private Point<Float> accelerometer;
    private Point<Float> geomagnetic;
    private Point<Long> orientationDegree;
    private Point<Float> tempForTransformation;

    //other
    private Context context;
    private String TAG = "myLog";
    private float[] orientation;
    private float[] rotationMatrix;
    private long start;
    private long finish;
    private float time;
    private int counter = 0;
    private int counterClbr = 0;
    private long timestamp;
    private float step;
    private boolean flagStartCalibration;
    private String filename = "distance.txt";
    private OutputStream os;


    public ReadSensors(Context c, boolean flagCalibr) throws IOException {
        flagStartCalibration = flagCalibr;
        context = c;
        speed = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        distance = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        dPoint = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        sumPoint = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        rotationMatrix = new float[16];
        accelerometer = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        geomagnetic = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        gravity = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
        orientationDegree = new Point<Long>((long) 0, (long) 0, (long) 0);
        orientation = new float[3];
        tempForTransformation = new Point<Float>((float) 0.0, (float) 0.0, (float) 0.0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Send data to file
    public void sendData() {
        String string = String.valueOf(distance.getX()) + "/" + String.valueOf(distance.getY()) + "/" + String.valueOf(distance.getZ());
        byte[] buffer = string.getBytes();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("myLogs", "don't permission");
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("myLogs", "have permission");
            File file = new File("/sdcard/" + filename);
            try {
                os = new FileOutputStream(file);
                os.write(buffer);
            } catch (IOException e) {

                Log.d("ExternalStorage123", "Error writing " + file, e);
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            if (flagStartCalibration == false) {
                if (counterClbr <= BUFFERCLBR) {
                    calibration(event);
                    Intent in = new Intent("EndCalibration");
                    in.putExtra("answer", false);
                    context.sendBroadcast(in);

                } else {
                    Intent in = new Intent("EndCalibration");
                    in.putExtra("answer", true);
                    context.sendBroadcast(in);
                    flagStartCalibration = true;
                }
            } else {
                Intent in = new Intent("flag0");
                in.putExtra("Data", "false");
                writeValueSensor(event);

            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravity.set(event.values[0], event.values[1], event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic.set(event.values[0], event.values[1], event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            timestamp = event.timestamp;
            step = event.values[0];

        } else step = 0;

    }

    //Transform vector of values (x,y,z) to world coordinate system
    public Point<Float> transformationToWorldCS(SensorEvent event) {
        float[] deviceAcceleration = new float[4];
        deviceAcceleration[0] = event.values[0];
        deviceAcceleration[1] = event.values[1];
        deviceAcceleration[2] = event.values[2];
        deviceAcceleration[3] = 0;

        float[] R = new float[16], I = new float[16], earthAcc = new float[16];
        float[] gravityValues = {gravity.getX(), gravity.getY(), gravity.getZ()};
        float[] magneticValues = {geomagnetic.getX(), geomagnetic.getY(), geomagnetic.getZ()};
        SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);
        float[] inv = new float[16];
        android.opengl.Matrix.invertM(inv, 0, R, 0);
        android.opengl.Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceAcceleration, 0);
        return new Point(earthAcc[0], earthAcc[1], earthAcc[2]);
    }

    //Average countiong of acceleration every 50 measurements
    public void writeValueSensor(SensorEvent event) {
        currentPoint = transformationToWorldCS(event);
        if (counter <= BUFFER) {
            if (counter == 0) {
                sumPoint = new Point(0, 0, 0);
                start = System.currentTimeMillis();
                sumPoint = currentPoint;
                counter++;
            } else {
                sumPoint.writeAdd(currentPoint.getX(), currentPoint.getY(), currentPoint.getZ());
                counter++;
                if (counter == BUFFER) {
                    finish = System.currentTimeMillis();
                    avgPoint = new Point(round((sumPoint.getX() / BUFFER) - dPoint.getX()),
                            round((sumPoint.getY() / BUFFER) - dPoint.getY()),
                            round((sumPoint.getZ() / BUFFER) - dPoint.getZ()));
                    round(avgPoint);
                    time = (float) (finish - start) / 1000;
                    if ((avgPoint.getX() == 0) && (avgPoint.getY() == 0) && (avgPoint.getZ() == 0)) {
                        //проверяем есть ли вообще движение
                        if (step == 1.0)//движение есть, просто оно равномерное
                        {
                            calcDistEven();
                        } else //движения нет, и мы не считаем расстояние, а отправляем 0
                        {
                            setToZeroDist();
                        }
                    } else {
                        calcDistUneven();
                    }
                    sendData();
                    counter = 0;
                }
            }

        }
    }

//No movement
    public void setToZeroDist() {
        distance.set((float) 0.0, (float) 0.0, (float) 0.0);
        speed.set((float) 0.0, (float) 0.0, (float) 0.0);
    }

    //Even motion
    public void calcDistEven() {

        //скорость уже известна раньше и имеет некую величину
        distance.setX(round(speed.getX() * time));
        distance.setX(round(speed.getY() * time));
        distance.setX(round(speed.getZ() * time));
    }

    //Uneven movement
    public void calcDistUneven() {
        distance.setX(round(speed.getX() * time + avgPoint.getX() * time * time / 2));
        distance.setY(round(speed.getY() * time + avgPoint.getY() * time * time / 2));
        distance.setZ(round(speed.getZ() * time + avgPoint.getZ() * time * time / 2));
        speed.setX(round(speed.getX() + avgPoint.getX() * time));
        speed.setY(round(speed.getY() + avgPoint.getY() * time));
        speed.setZ(round(speed.getZ() + avgPoint.getZ() * time));
    }

    private float round(float num) {
        num = num * 100;
        int i = (int) Math.round(num);
        return (float) i / 100;
    }

    //Delete innacurancies
    private void round(Point<Float> p) {
        if ((p.getX() < 0.5) && (p.getX() > 0.0)) {
            p.setX((float) 0.0);
        } else if ((p.getX() < 0.0) && (p.getX() > -0.5)) {
            p.setX((float) 0.0);
        }
        if ((p.getY() < 0.5) && (p.getY() > 0.0)) {
            p.setY((float) 0.0);
        } else if ((p.getY() < 0.0) && (p.getY() > -0.5)) {
            p.setY((float) 0.0);
        }
        if ((p.getZ() < 0.5) && (p.getZ() > 0.0)) {
            p.setZ((float) 0.0);
        } else if ((p.getZ() < 0.0) && (p.getZ() > -0.5)) {
            p.setZ((float) 0.0);
        }
    }

    //Calibration
    private void calibration(SensorEvent event) {
        tempForTransformation = transformationToWorldCS(event);
        dPoint.writeAdd(tempForTransformation.getX(), tempForTransformation.getY(), tempForTransformation.getZ());
        if (counterClbr == BUFFERCLBR) {
            dPoint.set(dPoint.getX() / BUFFERCLBR,
                    dPoint.getY() / BUFFERCLBR,
                    dPoint.getZ() / BUFFERCLBR);
        }
        counterClbr++;
    }


}
