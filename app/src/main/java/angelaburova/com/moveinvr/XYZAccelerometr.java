package angelaburova.com.moveinvr;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by angelaburova on 28.10.2017.
 */

public class XYZAccelerometr implements SensorEventListener {
    private static final int BUFFER = 20;
    private static final int BUFFERCLBR = 150;
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
    private long timestamp;
    private int step;
    private int counterSteps;
    private Point gravity;

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
                Intent in = new Intent("flag0");
                in.putExtra("Data", "false");
                writeValueSensor(event);
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.d(LOG_TAG, "step");
            timestamp = event.timestamp;
            step = (int) event.values[0];
            Intent in = new Intent("step");
            in.putExtra("Data", step);
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravity = new Point(event.values[1], event.values[0], event.values[2]);
            //т.к. у нас другая система координат
        }
    }

    public Point checkAxis(SensorEvent event) {
        gravity.set((int) gravity.getX(), (int) gravity.getY(), (int) gravity.getZ());
        Point correctCrdnts = new Point(event.values[1], event.values[0] * (-1), event.values[2]);
        //сконвертируем оси, т.к. нормальное состояние для нас не вертикальное,а горизонтальное
        //x=-y
        //y=x
        //z не меняется

        if (gravity.getX() != 0.0) //значит на ось икс действует g
        {
            if ((gravity.getY() == 0.0) && (gravity.getZ() == 0.0))//g действует только на x
            {
                if (gravity.getX() < 0) {
                    correctCrdnts.set(correctCrdnts.getY() * (-1), correctCrdnts.getX(), correctCrdnts.getZ());
                    return correctCrdnts;
                } else if (gravity.getX() > 0) {
                    correctCrdnts.set(correctCrdnts.getY(), correctCrdnts.getX() * (-1), correctCrdnts.getZ());
                    return correctCrdnts;
                }
            }
            //на другие оси тоже действует, правим только x
        }
        if (gravity.getZ() != 0.0)//значит на ось z действует g
        {
            if ((gravity.getY() == 0.0) && (gravity.getX() == 0.0))//g действует только на z
            {
                if (gravity.getZ() > 0) {
                    correctCrdnts.set(correctCrdnts.getX(),correctCrdnts.getZ(),correctCrdnts.getY()*(-1));
                    return correctCrdnts;

                } else if (gravity.getZ() < 0) {
                    correctCrdnts.set(correctCrdnts.getX(),correctCrdnts.getZ()*(-1),correctCrdnts.getY());
                    return correctCrdnts;
                }
            }
            //на другие оси тоже действует, правим только z
        }
        if (gravity.getY() != 0.0)//значит на ось y действует g
        {
            if ((gravity.getZ() == 0.0) && (gravity.getX() == 0.0))//g действует только на y
            {
                if (gravity.getY() < 0)//y направлена вниз
                {
                    correctCrdnts.set(correctCrdnts.getX() * (-1), correctCrdnts.getY() * (-1), correctCrdnts.getZ());
                    return correctCrdnts;
                } else if (gravity.getY() > 0)//y направлена вверх
                {
                    return correctCrdnts;
                }
            }
            //на другие оси тоже действует, правим только y
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
        in.putExtra("Data", gravity.getArray());
        context.sendBroadcast(in);
    }

    public void writeValueSensor(SensorEvent event) {
        currentPoint = new Point(event.values[1], event.values[0] * (-1), event.values[2]);
        //currentPoint=checkAxis(event);
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
                    calcDist();
                    sendData();
                    counter = 0;
                }
            }

        }


    }


    public void calcDist() {
        time = (double) (finish - start) / 1000;
        if ((avgPoint.getX() == 0.0) && (avgPoint.getY() == 0.0) && (avgPoint.getY() == 0.0)) {
            speed.set(0.0, 0.0, 0.0);
        }
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

    public void round(Point p) {
        if ((p.getX() < 0.5) && (p.getX() > 0.0)) {
            p.setX(0.0);
        } else if ((p.getX() < 0.0) && (p.getX() > -0.5)) {
            p.setX(0.0);
        }
        if ((p.getY() < 0.5) && (p.getY() > 0.0)) {
            p.setY(0.0);
        } else if ((p.getY() < 0.0) && (p.getY() > -0.5)) {
            p.setY(0.0);
        }
        if ((p.getZ() < 0.5) && (p.getZ() > 0.0)) {
            p.setZ(0.0);
        } else if ((p.getZ() < 0.0) && (p.getZ() > -0.5)) {
            p.setZ(0.0);
        }
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
