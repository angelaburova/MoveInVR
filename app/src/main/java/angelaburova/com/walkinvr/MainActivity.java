package angelaburova.com.walkinvr;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.graphics.drawable.RippleDrawable;
import 	android.graphics.drawable.GradientDrawable;

import angelaburova.com.moveinvr.R;

public class MainActivity extends AppCompatActivity {

    private Button btnStart,btnCalibr,btnSett;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);
        btnStart=(Button) findViewById(R.id.btnStart);
        btnStart.setClickable(false);
        setFonts();


    }

    public void setFonts()
    {

        btnCalibr=(Button)findViewById(R.id.btnCalibr);
        btnSett=(Button)findViewById(R.id.btnSett);
        Typeface type = Typeface.createFromAsset(getAssets(),"font.otf");
        btnCalibr.setTypeface(type);
        btnStart.setTypeface(type);
        btnSett.setTypeface(type);
    }

    public void openTwoActivity(View view){
        Intent intent = new Intent(this, TwoActivity.class);
        startActivity(intent);
    }

    public void buttonStartClick(View v)
    {
        startService(new Intent(this, ServiceAcc.class));
        //вызывать toast - всплывающие уведомления или диалоговые окна, с просьбой положить телефон на стол и нажать ок
        //затем проихводится калибровка, крутится кругляшок. как только закончилась  - вызываем приложение с виртуальным пространством


    }
    public void buttonQuitClick(View v)
    {
        //stop service
        stopService(new Intent(this, ServiceAcc.class));

    }

    public void buttonSettingsClick(View v)
    {


    }


}
