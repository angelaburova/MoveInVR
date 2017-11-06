package angelaburova.com.moveinvr;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;


/**
 * Created by angelaburova on 28.10.2017.
 */

public class XYZAccelerometr extends Accelerometr {
    private static final int BUFFER = 50;
    private static final int BUFFERCLBR = 300;
    private Point currentPoint;
    private Point sumPoint;
    public static Point avgPoint;
    private String LOG_TAG = "myLog";
    private Context context;
    private long start;
    private long finish;
    private double time;
    private Point distance;
    private Point speed;
    private Point dPoint;
    private int counter = 0;
    private int counterClbr = 0;

    public XYZAccelerometr(Context c) {
        context = c;
        speed = new Point(0, 0, 0);
        distance = new Point(0, 0, 0);
        dPoint = new Point(0, 0, 0);
        sumPoint = new Point(0, 0, 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (counterClbr <= BUFFERCLBR) {
                calibration(event);
            } else {
                writeValueSensor(event);
            }
        }
    }

    public void sendData() {
        Intent in = new Intent("Acceleration");
        in.putExtra("Data", avgPoint.getArray());
        context.sendBroadcast(in);
        in = new Intent("Speed");
        in.putExtra("Data", speed.getArray());
        context.sendBroadcast(in);
        in = new Intent("Distance");
        in.putExtra("Data", distance.getArray());
        context.sendBroadcast(in);
    }

    public void writeValueSensor(SensorEvent event) {
        currentPoint = new Point(round(event.values[0]), round(event.values[1]), round(event.values[2]));
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
                    calcDist();
                    sendData();
                    counter = 0;
                }
            }

        }


    }


    public void calcDist() {
        time = (double) (finish - start) / 1000;
        distance.setX(round(speed.getX() * time + avgPoint.getX() * time * time / 2));
        distance.setY(round(speed.getY() * time + avgPoint.getY() * time * time / 2));
        distance.setZ(round(speed.getZ() * time * +avgPoint.getZ() * time * time / 2));
        speed.setX(round(speed.getX() + avgPoint.getX() * time));
        speed.setY(round(speed.getY() + avgPoint.getY() * time));
        speed.setZ(round(speed.getZ() + avgPoint.getZ() * time));
    }

    public double round(double num) {
        num = num * 100;
        int i = (int) Math.round(num);
        return (double) i / 100;
    }

    public void calibration(SensorEvent event) {
        dPoint.writeAdd(event.values[0], event.values[1], event.values[2]);
        if (counterClbr == BUFFERCLBR) {
            dPoint.set((double) dPoint.getX() / BUFFERCLBR,
                    (double) dPoint.getY() / BUFFERCLBR,
                    (double) dPoint.getZ() / BUFFERCLBR);
        }
        counterClbr++;
    }

}
