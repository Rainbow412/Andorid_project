package android.project;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MusicWidget extends AppWidgetProvider {

    private String NOTIACTION = "android.project.notification";
    private String CACELNOTI = "android.project.cancelnotification";
    private String PLAYPAUSE = "android.project.playpause";
    private String STOP = "android.project.stop";
    private String title = "";
    private String artist = "";
    private int position = 0;
    private RemoteViews rv = new RemoteViews("android.project", R.layout.music_widget);

//    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                int appWidgetId) {
//
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
//        views.setTextViewText(R.id.wid_song, "歌曲名");
//        views.setTextViewText(R.id.wid_singer, "歌手");
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //先初始化Widget
        rv.setTextViewText(R.id.wid_song, "点击播放音乐");
        rv.setTextViewText(R.id.wid_singer, "");
        rv.setViewVisibility(R.id.wid_play_pause, 4);

        Intent clickInt = new Intent(context, Player.class);
        clickInt.putExtra("position",position);
        PendingIntent pi = PendingIntent.getActivity(context, 1, clickInt, 0);
        rv.setOnClickPendingIntent(R.id.widget, pi);
//        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.music_widget);

        //点击播放暂停键
        Intent playIntent = new Intent(PLAYPAUSE);
        PendingIntent playPending = PendingIntent.getBroadcast(context, 0 , playIntent, 0);
        rv.setOnClickPendingIntent(R.id.wid_play_pause, playPending);

        //更新widget
        appWidgetManager.updateAppWidget(appWidgetIds, rv);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        if (intent.getAction().equals(NOTIACTION)) {
            Log.d("Receiver in wid", "NOTIACTION");
            Bundle bundle = intent.getExtras();
            title = bundle.getString("title");
            artist = bundle.getString("artist");
            position = bundle.getInt("position");

            rv.setTextViewText(R.id.wid_song, title);
            rv.setTextViewText(R.id.wid_singer, artist);
            rv.setViewVisibility(R.id.wid_play_pause, 0);
            rv.setImageViewResource(R.id.wid_play_pause, R.mipmap.pause);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MusicWidget.class.getName());
            //更新widget
            appWidgetManager.updateAppWidget(thisAppWidget, rv);
        }
        else if(intent.getAction().equals(PLAYPAUSE)){
            Log.d("Receiver in wid", "PLAYPAUSE");
            if(MusicService.mp.isPlaying()){
                rv.setImageViewResource(R.id.wid_play_pause, R.mipmap.pause);
            } else{
                rv.setImageViewResource(R.id.wid_play_pause, R.mipmap.play);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MusicWidget.class.getName());
            //更新widget
            appWidgetManager.updateAppWidget(thisAppWidget, rv);
        }
        else if(intent.getAction().equals(STOP)){
            Log.d("Receiver in wid", "STOP");
            if(MusicService.mp!=null) {
                rv.setImageViewResource(R.id.wid_play_pause, R.mipmap.play);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MusicWidget.class.getName());
            //更新widget
            appWidgetManager.updateAppWidget(thisAppWidget, rv);
        }
        else if (intent.getAction().equals(CACELNOTI)){
            Log.d("Receiver in wid", "CACELNOTI");

            rv.setTextViewText(R.id.wid_song, "点击播放音乐");
            rv.setTextViewText(R.id.wid_singer, "");
            rv.setViewVisibility(R.id.wid_play_pause, 4);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MusicWidget.class.getName());
            //更新widget
            appWidgetManager.updateAppWidget(thisAppWidget, rv);
        }
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

