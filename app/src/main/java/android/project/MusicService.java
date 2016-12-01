package android.project;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MusicService extends Service {
    public static MediaPlayer mp=new MediaPlayer();
    private String path;

    public final IBinder binder=new MyBinder();
    @Override
    public IBinder onBind(Intent intent) {
        path=intent.getStringExtra("url");
        //System.out.println("sevice---------------"+path);
        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binder;
    }
    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public MusicService() {
        try{
            //File file=new File(Environment.getExternalStorageDirectory(),"/music/melt.mp3");
            //File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            //mp.setDataSource(file+"/melt.mp3");
            //mp.reset();
            mp.prepare();
            mp.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void play(){
        if(mp.isPlaying()){
            mp.pause();
        } else{
            mp.start();
        }
    }

    public void stop(){
        if(mp!=null) {
            mp.stop();
            try {
                mp.prepare();
                mp.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
