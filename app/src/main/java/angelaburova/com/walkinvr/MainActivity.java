package angelaburova.com.walkinvr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import angelaburova.com.moveinvr.R;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnQuit, btnSett;
    private boolean flagCalibration = false;
    private ProgressDialog pd;
    private Handler handler;
    private BroadcastReceiver br;
    private IntentFilter filter;
    private boolean endCalibration;
    private int PERMISSION_REQUEST_CODE;
    private int language = 0;
    private boolean music = true, sound = true;
    private SoundPool sp;
    private int soundID;
    private boolean loaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Request permissions
        requestMultiplePermissions();

        //Get data from settings activity
        Intent intent = getIntent();
        language = intent.getIntExtra("lang", 0);
        music = intent.getBooleanExtra("music", true);
        sound = intent.getBooleanExtra("sound", true);

        //Start foregroung music
        if (music == true) startService(new Intent(this, MusicService.class));

        //Set sound on click buttons
        setsoundButton();

        // Checking finish of calibration and start VR space
        syncServiceCalibration();

        // Delete title-bar
        deleteTitleBar();
        setContentView(R.layout.activity_main);

        // Set fonts for app
        setFonts();

    }

    //Delete title bar
    public void deleteTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Check the finish of calibration and start VR space
    public void syncServiceCalibration() {
        filter = new IntentFilter();
        filter.addAction("EndCalibration");
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("EndCalibration")) {
                    endCalibration = intent.getBooleanExtra("answer", true);
                    if (endCalibration == true) {
                        Toast.makeText(MainActivity.this, "Open virtual reality", Toast.LENGTH_SHORT).show();
                        Intent intentVR = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.angb.trees");

                        //Send data to VR space
                        intentVR.putExtra("getString", String.valueOf(music));
                        intentVR.putExtra("sound", String.valueOf(sound));
                        if (intentVR == null) Log.d("myLog", "VR = null");
                        else {
                            startActivity(intentVR);
                        }

                    }

                }
            }
        };
        registerReceiver(br, filter);
    }

    //Set soung on click button
    public void setsoundButton() {
        if (sound == true) {
            this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = true;
                }
            });

            soundID = sp.load(this, R.raw.soundclick, 1);
        }
    }

    //Request permissions
    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAPTURE_AUDIO_OUTPUT
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Set fonts for elements
    public void setFonts() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnQuit = (Button) findViewById(R.id.btnQuit);
        btnSett = (Button) findViewById(R.id.btnSett);
        Typeface type = Typeface.createFromAsset(getAssets(), "font.otf");
        btnQuit.setTypeface(type);
        btnStart.setTypeface(type);
        btnSett.setTypeface(type);
    }

    //On click button start
    public void buttonStartClick(View v) {
        if(sound==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        if (flagCalibration == false) {
            //Show dialog for calibration
            showAlertDialogCalibration();
        }

    }
    //Get volume
    public float getActualVolume(){
        AudioManager audioManager=(AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume=(float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume=(float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume/ maxVolume;
    }

    //Show dialog for calibration
    public void showAlertDialogCalibration() {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.sample, null);
        alertadd.setView(view);
        alertadd.setTitle(R.string.calibration)
                .setMessage(R.string.description)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startCalibration();
                                dialog.cancel();
                            }
                        });


        alertadd.show();
    }

    //Show progress dialog and calibration
    public void startCalibration() {
        // Start service
        startService(new Intent(this, ServiceAcc.class).putExtra("calibration", false));
        Toast.makeText(MainActivity.this, "Start calibration", Toast.LENGTH_SHORT).show();
        // Create progress dialog
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle(R.string.calibration);
        pd.setMessage(this.getResources().getString(R.string.waiting));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setIndeterminate(true);
        pd.show();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                pd.setIndeterminate(false);
                if (pd.getProgress() < pd.getMax()) {
                    pd.incrementProgressBy(12);
                    pd.incrementSecondaryProgressBy(12);
                    handler.sendEmptyMessageDelayed(0, 100);
                } else {
                    pd.dismiss();
                }
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);

    }

    //On click button Quit
    public void buttonQuitClick(View v) {
        if(sound==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        // Stop service
        stopService(new Intent(this, MusicService.class));
        stopService(new Intent(this, ServiceAcc.class));
        Toast.makeText(MainActivity.this, "Close app", Toast.LENGTH_SHORT).show();
        finishAffinity();

    }
    //On click button seettings
    public void buttonSettingsClick(View v) {
        if(sound==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        Toast.makeText(MainActivity.this, "Open settings", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ActivitySettings.class);
        //Send data to settings activity
        intent.putExtra("lang", language);
        intent.putExtra("music",music);
        intent.putExtra("sound",sound);
        startActivity(intent);

    }

    @Override
    public void onPause(){
       // stopService(new Intent(this, MusicService.class));
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        startService(new Intent(this, MusicService.class));
    }
    @Override
    public void onStop(){
        //stopService(new Intent(this, MusicService.class));
        super.onStop();
    }

    // Вызывается перед выходом из "полноценного" состояния.
    @Override
    public void onDestroy(){
        stopService(new Intent(this, MusicService.class));
        super.onDestroy();
    }
}
