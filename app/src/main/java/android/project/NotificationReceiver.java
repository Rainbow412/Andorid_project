package android.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Created by 123 on 2016/12/12.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private String NOTIACTION = "android.project.notification";
    private String CACELNOTI = "android.project.cancelnotification";
    private String PRESONG = "android.project.presong";
    private String NEXTSONG = "android.project.nextsong";
    private String PLAYPAUSE = "android.project.playpause";
    private String title = "";
    private String artist = "";
    private int position;
    public NotificationManager manager;
    public RemoteViews remoteViews;
    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals(NOTIACTION)){
            Log.d("Receiver", "NOTIFICATION");
            Bundle bundle = intent.getExtras();
            title = bundle.getString("title");
            artist = bundle.getString("artist");
            position = bundle.getInt("position");

            remoteViews = new RemoteViews("android.project", R.layout.notification);
            remoteViews.setTextViewText(R.id.noti_song, title);
            remoteViews.setTextViewText(R.id.noti_singer, artist);
//            if (MusicService.mp.isPlaying()){
//                remoteViews.setImageViewResource(R.id.noti_play_pause, R.mipmap.pause);
//            }else {
//                remoteViews.setImageViewResource(R.id.noti_play_pause, R.mipmap.play);
//            }


            manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);

//            //点击上一首播放上一首音乐
//            Intent preIntent = new Intent(PRESONG);
//            PendingIntent prePending = PendingIntent.getBroadcast(context, 0 , preIntent, 0);
//            remoteViews.setOnClickPendingIntent(R.id.noti_pre, prePending);
//
//            //点击播放暂停键
//            Intent playIntent = new Intent(PLAYPAUSE);
//            PendingIntent playPending = PendingIntent.getBroadcast(context, 0 , playIntent, 0);
//            remoteViews.setOnClickPendingIntent(R.id.noti_play_pause, playPending);
//
//            //点击下一首
//            Intent nextIntent = new Intent(NEXTSONG);
//            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0 , nextIntent, 0);
//            remoteViews.setOnClickPendingIntent(R.id.noti_next, nextPending);

            //点击跳转至播放界面
            Intent mIntent = new Intent(context, Player.class);
            Log.d("position in NotiRec", ""+position);
            mIntent.putExtra("position",position);
            PendingIntent intent_go = PendingIntent.getActivity(context, 1 , mIntent, 0);
            builder.setContentIntent(intent_go);


            Notification notify = builder.build();
            notify.contentView = remoteViews;
            notify.flags = Notification.FLAG_ONGOING_EVENT;
            notify.icon = R.mipmap.icon;
            manager.notify(0, notify);

        }
        else if(intent.getAction().equals(CACELNOTI)){
            Log.d("Receiver", "CANCEL");
            manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
        }
//        else if(intent.getAction().equals(PRESONG)){
//            Log.d("Receiver", "PRESONG");
//            Player p = new Player();
//            p.preSong();
//        }
    }
}
