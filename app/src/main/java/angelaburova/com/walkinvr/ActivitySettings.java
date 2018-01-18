package angelaburova.com.walkinvr;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import angelaburova.com.moveinvr.R;

public class ActivitySettings extends AppCompatActivity {

    private TextView text1,text2,text3,text4;
    private ToggleButton tb;
    private Button btn;
    private android.support.v7.widget.SwitchCompat switchMusic, switchSound;
    private int language=0;
    private Intent intentGet,intentSet;
    private SoundPool sp;
    private boolean loaded = false;
    private int soundID;
    private boolean soundOn=true;
    private boolean musicOn=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Delete title bar
        deleteTitleBar();
        setContentView(R.layout.activity_settings);
        //Initialize elements
        initComponents();
        //Get data from main activity
        intentGet = getIntent();
        language = intentGet.getIntExtra("lang", 0);
        soundOn=intentGet.getBooleanExtra("sound",true);
        musicOn=intentGet.getBooleanExtra("music",true);
        //Set sound on click buttons
        setsoundButton();
        //Check switch
        if (soundOn == true) {
            switchSound.setChecked(true);
        }else switchSound.setChecked(false);
        if (musicOn == true) {
            switchMusic.setChecked(true);
        }else switchMusic.setChecked(false);
        //Set language
        setLanguage();
        //Set fonts for elements
        setFonts();
    }

    public void initComponents(){
        intentSet = new Intent(this, MainActivity.class);
        switchMusic=(android.support.v7.widget.SwitchCompat) findViewById(R.id.switch1);
        switchSound=(android.support.v7.widget.SwitchCompat) findViewById(R.id.switchCompat);
        text1=(TextView) findViewById(R.id.sound);
        text2=(TextView)findViewById(R.id.music);
        text3=(TextView)findViewById(R.id.settings);
        text4=(TextView)findViewById(R.id.language);
        btn=(Button) findViewById(R.id.button1);
        tb = (ToggleButton) findViewById(R.id.lang);
    }

    //Set checked toggle button
    public void setLanguage(){
        if (language == 0) {
            if (getResources().getConfiguration().locale.getLanguage() == "ru") {
                tb.setChecked(true);
            } else {
                tb.setChecked(false);
            }
        }
        else
        {
            if (language == 1) {
                tb.setChecked(true);
            } else {
                tb.setChecked(false);
            }
        }
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

    //On click toogle button language. Change locale
    public void langButtonOnClick(View v){
        if(soundOn==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        Locale locale;
      if(tb.isChecked()==true) {
          locale = new Locale("ru");
          language=1;
      } else {
          locale = new Locale("en");
          language=2;
      }
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }
    //Set fonts for elements
    public void setFonts()
    {
        Typeface type = Typeface.createFromAsset(getAssets(),"font.otf");
        text1.setTypeface(type);
        text2.setTypeface(type);
        text3.setTypeface(type);
        text4.setTypeface(type);
        tb.setTypeface(type);
        btn.setTypeface(type);
    }

    //On click music switch
    public void switchMusicClick(View v){
        if(soundOn==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);

        if(switchMusic.isChecked()==true){
            musicOn=true;
            startService(new Intent(this, MusicService.class));
        }
        else {
            musicOn=false;
            stopService(new Intent(this, MusicService.class));
        }
    }

    //On click sound switch
    public void switchSoundClick(View v){
        if(soundOn==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        if(switchSound.isChecked()==true){
            soundOn=true;
        }
        else {
            soundOn=false;
        }

    }

    //On click button menu
    public void menuButtonClick(View v)
    {
        if(soundOn==true)sp.play(soundID, getActualVolume(), getActualVolume(),1,0, 1f);
        intentSet.putExtra("music",musicOn);
        intentSet.putExtra("lang",language);
        intentSet.putExtra("sound",soundOn);
        startActivity(intentSet);
    }

    //Set sound on click button
    public void setsoundButton() {

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

    //Get volume
    public float getActualVolume(){
        AudioManager audioManager=(AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume=(float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume=(float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume/ maxVolume;
    }
}
