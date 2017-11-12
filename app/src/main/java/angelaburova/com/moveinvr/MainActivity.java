package angelaburova.com.moveinvr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView[] text;
    private String LOG_TAG = "myLog";
    private BroadcastReceiver br;
    private String flag;
    private float step;
    private double[] valuesAcc, valuesSpd, valuesDist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = new TextView[11];
        text[0] = (TextView) findViewById(R.id.number1);//binding
        text[1] = (TextView) findViewById(R.id.number2);
        text[2] = (TextView) findViewById(R.id.number3);
        text[3] = (TextView) findViewById(R.id.number4);
        text[4] = (TextView) findViewById(R.id.number5);
        text[5] = (TextView) findViewById(R.id.number6);
        text[6] = (TextView) findViewById(R.id.number7);
        text[7] = (TextView) findViewById(R.id.number8);
        text[8] = (TextView) findViewById(R.id.number9);
        text[9] = (TextView) findViewById(R.id.number10);
        text[10] = (TextView) findViewById(R.id.number11);
        valuesAcc = new double[3];
        valuesSpd = new double[3];
        valuesDist = new double[3];

        IntentFilter filter = new IntentFilter();
        filter.addAction("Acceleration");
        filter.addAction("Speed");
        filter.addAction("Distance");
        filter.addAction("flag0");
        filter.addAction("step");
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Acceleration")) {
                    valuesAcc = intent.getDoubleArrayExtra("Data");
                }
                if (intent.getAction().equals("Speed")) {
                    valuesSpd = intent.getDoubleArrayExtra("Data");
                }
                if (intent.getAction().equals("Distance")) {
                    valuesDist = intent.getDoubleArrayExtra("Data");
                }
                if(intent.getAction().equals("flag0"))
                {
                    Log.d(LOG_TAG,"flag0");
                    flag=intent.getStringExtra("Data");
                }
                if(intent.getAction().equals("step"))
                {
                    step=intent.getFloatExtra("Data",0);
                }
                print();

            }
        };
        registerReceiver(br, filter);
        startService(new Intent(this, ServiceAcc.class));
    }

    public void print() {
        text[0].setText("AccX= " + String.valueOf(valuesAcc[0]));
        text[1].setText("AccY= " + String.valueOf(valuesAcc[1]));
        text[2].setText("AccZ= " + String.valueOf(valuesAcc[2]));
        text[3].setText("SpeedX= " + String.valueOf(valuesSpd[0]));
        text[4].setText("SpeedY= " + String.valueOf(valuesSpd[1]));
        text[5].setText("SpeedZ= " + String.valueOf(valuesSpd[2]));
        text[6].setText("DistX= " + String.valueOf(valuesDist[0]));
        text[7].setText("DistY= " + String.valueOf(valuesDist[1]));
        text[8].setText("DistZ= " + String.valueOf(valuesDist[2]));
        text[9].setText("OK. Calibration finised. You can start moving");
        text[10].setText("step= " + step);
    }

    public Context getCont() {
        return this;
    }

    protected void onStart() {
        super.onStart();

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (br != null) {
            unregisterReceiver(br);
        }
        stopService(new Intent(this, ServiceAcc.class));
    }


}
