package angelaburova.com.walkinvr;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.hardware.SensorManager;

/**
 * Created by angelaburova on 28.10.2017.
 */

public class XYZAccelerometr implements SensorEventListener {
    private static final int BUFFER = 20;
    private static final int BUFFERCLBR = 150;
    private Point<Float> currentPoint;
    private Point<Float> sumPoint;
    public  Point<Double> avgPoint;
    private String LOG_TAG = "myLog";
    private Context context;
    private Point<Double> distance;
    private Point<Double> speed;
    private Point<Float> dPoint;
    private Point<Integer> gravity;
    private Point<Float> accelerometer;
    private Point<Float> geomagnetic;
    private Point<Long> orientationDegree;
    private float[] orientation;
    private float[] rotationMatrix;
    private long start;
    private long finish;
    private double time;
    private int counter = 0;
    private int counterClbr = 0;
    private long timestamp;
    private int step;
    private int counterSteps;


    public XYZAccelerometr(Context c) {
        context = c;
        speed = new Point<Double>(0.0, 0.0, 0.0);
        distance = new Point<Double>(0.0, 0.0, 0.0);
        dPoint = new Point<Float>((float)0.0,(float)0.0,(float)0.0);
        sumPoint = new Point<Float>((float)0.0,(float)0.0,(float)0.0);
        rotationMatrix=new float[16];
        accelerometer=new Point<Float> ((float)0.0,(float)0.0,(float)0.0);
        geomagnetic=new Point<Float> ((float)0.0,(float)0.0,(float)0.0);
        gravity=new Point<Integer>(0,0,0);
        orientationDegree=new Point<Long>((long)0,(long)0,(long)0);
        orientation=new float[3];

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

        getRotation(event);
    }

    public void getRotation(SensorEvent event)
    {

        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            geomagnetic.set(event.values[0],event.values[1],event.values[2]);
        }

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            accelerometer.set(event.values[0],event.values[1],event.values[2]);
        }
        float[] accArray={accelerometer.getX(),accelerometer.getY(),accelerometer.getZ()};
        float[] geoArray={geomagnetic.getX(),geomagnetic.getY(),geomagnetic.getZ()};
        SensorManager.getRotationMatrix(rotationMatrix, null,accArray,geoArray);//Получаем матрицу поворота
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;


        SensorManager.getOrientation(rotationMatrix, orientation);//Получаем данные ориентации устройства в пространстве
        orientationDegree.set(Math.round(Math.toDegrees(orientation[0])),Math.round(Math.toDegrees(orientation[1])),Math.round(Math.toDegrees(orientation[2])));


    }
    public Point checkAxis(SensorEvent event) {
        gravity.set((int) gravity.getX(), (int) gravity.getY(), (int) gravity.getZ());
        Point<Float> correctCrdnts = new Point<Float>(event.values[1], event.values[0] * (-1), event.values[2]);
        //сконвертируем оси, т.к. нормальное состояние для нас не вертикальное,а горизонтальное
        //x=-y
        //y=x
        //z не меняется

        if ((gravity.getX()!= 0)&&(gravity.getY()!=0)&&(gravity.getZ()!=0)){
            //в этом случае мы должны считывать углы поворота экрана
            return correctCrdnts;
        }
        else {
            if (gravity.getX() != 0) //значит на ось икс действует g
            {
                if ((gravity.getY() == 0) && (gravity.getZ() == 0))//g действует только на x
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
            else if (gravity.getZ() != 0)//значит на ось z действует g
            {
                if ((gravity.getY() == 0) && (gravity.getX() == 0))//g действует только на z
                {
                    if (gravity.getZ() > 0) {
                        correctCrdnts.set(correctCrdnts.getX(), correctCrdnts.getZ(), correctCrdnts.getY() * (-1));
                        return correctCrdnts;

                    } else if (gravity.getZ() < 0) {
                        correctCrdnts.set(correctCrdnts.getX(), correctCrdnts.getZ() * (-1), correctCrdnts.getY());
                        return correctCrdnts;
                    }
                }
                //на другие оси тоже действует, правим только z
            }
            else if (gravity.getY() != 0)//значит на ось y действует g
            {
                if ((gravity.getZ() == 0) && (gravity.getX() == 0))//g действует только на y
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
        return correctCrdnts;
    }

    public void sendData() {
        Intent in = new Intent("Acceleration");
        double[] avg={avgPoint.getX(),avgPoint.getY(),avgPoint.getZ()};
        in.putExtra("Data", avg);
        context.sendBroadcast(in);
        in = new Intent("Speed");
        double[] speedArr={speed.getX(),speed.getY(),speed.getZ()};
        in.putExtra("Data", speedArr);
        context.sendBroadcast(in);
        in = new Intent("Distance");
        long[] orient={orientationDegree.getX(),orientationDegree.getY(),orientationDegree.getZ()};
        in.putExtra("Data", orient);
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

    private double round(double num) {
        num = num * 100;
        int i = (int) Math.round(num);
        return (double) i / 100;
    }

    private void round(Point<Double> p) {
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

    private void calibration(SensorEvent event) {
        dPoint.writeAdd(event.values[0], event.values[1], event.values[2]);
        if (counterClbr == BUFFERCLBR) {
            dPoint.set( dPoint.getX() / BUFFERCLBR,
                     dPoint.getY() / BUFFERCLBR,
                     dPoint.getZ() / BUFFERCLBR);
        }
        counterClbr++;
    }


}
