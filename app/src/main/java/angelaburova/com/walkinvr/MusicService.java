package angelaburova.com.walkinvr;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import angelaburova.com.moveinvr.R;

public class MusicService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("musicService","start");
        player = MediaPlayer.create(this, R.raw.music);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        Log.d("musicService","oNstartCommand");
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
            player.stop();
        return null;
    }

    public void onStop() {
        player.stop();
    }
    public void onPause() {
        player.stop();
    }
    @Override
    public void onDestroy() {

        player.stop();
        player.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onLowMemory() {

            player.stop();
    }

}