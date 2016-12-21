package android.project;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    public static MediaPlayer mp=new MediaPlayer();
    private String path;
    private String title;
    private String artist;
    private int position;
    //private String position;


    private String NOTIACTION = "android.project.notification";
    private String PLAYPAUSE = "android.project.playpause";
    private String STOP = "android.project.stop";

    public final IBinder binder=new MyBinder();
    @Override
    public IBinder onBind(Intent intent) {
//        path=intent.getStringExtra("url");
        Bundle bundle = intent.getExtras();
        path = bundle.getString("url");
        title = bundle.getString("title");
        artist = bundle.getString("artist");
        position = bundle.getInt("position");
        Log.d("position in MS", ""+position);

        //position=intent.getStringExtra("position");
        //System.out.println("sevice---------------"+path);

        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
            notificationBroadcast();
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
            //mp.setDataSource(path);
            //mp.prepare();
            //mp.start();
            mp.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void play(){
//        if(mp.isPlaying()){
//            mp.pause();
//        } else{
//            mp.start();
//        }
        Intent intent = new Intent(PLAYPAUSE);
        sendBroadcast(intent);
    }

    public void stop(){
//        if(mp!=null) {
//            mp.stop();
//            try {
//                mp.prepare();
//                mp.seekTo(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        Intent intent = new Intent(STOP);
        sendBroadcast(intent);
    }

    void notificationBroadcast(){
        Intent intent = new Intent(NOTIACTION);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("artist", artist);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }



    /*public void next() {
        if(mp != null && musicIndex < 3) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex+1]);
                musicIndex++;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                //Log.d("hint", "can't jump next music");
                e.printStackTrace();
            }
        }
    }
    public void pre() {
        if(mp != null && musicIndex > 0) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex-1]);
                musicIndex--;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                //Log.d("hint", "can't jump pre music");
                e.printStackTrace();
            }
        }
    }*/









}
