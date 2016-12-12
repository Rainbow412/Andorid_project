package android.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by 123 on 2016/12/12.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private String NOTIACTION = "android.project.notification";
    private String title = "";
    private String artist = "";
    private int position;
    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals(NOTIACTION)){
            Bundle bundle = intent.getExtras();
            title = bundle.getString("title");
            artist = bundle.getString("artist");
            position = bundle.getInt("position");

            RemoteViews remoteViews = new RemoteViews("android.project", R.layout.notification);
            remoteViews.setTextViewText(R.id.noti_song, title);
            remoteViews.setTextViewText(R.id.noti_singer, artist);
//            if (MusicService.mp.isPlaying()){
//                remoteViews.setImageViewResource(R.id.noti_play_pause, R.mipmap.pause);
//            }else {
//                remoteViews.setImageViewResource(R.id.noti_play_pause, R.mipmap.play);
//            }

            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);

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
    }
}
