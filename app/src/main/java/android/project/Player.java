package android.project;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class Player extends AppCompatActivity {
    private Button play;
    private Button stop;
    private Button quit;
    private MusicService ms = null;
    private TextView state;
    private TextView current;
    private TextView total;
    private SeekBar seekBar;
    private SimpleDateFormat mt;
    private Button pre;
    private Button next;


    boolean starting=false;
    private TextView t;
    private TextView a;
    private String url;
    private int position;
    private Music music;
    private List<Info> infos;
    private String u[];
    private String artist[];
    private String title[];
    private int size;

    private Intent intent;

    private final class InnerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (position >= size-1){
                state.setText("已停止");
                play.setBackground(getResources().getDrawable(R.mipmap.play));
                ms.stop();
                seekBar.setProgress(0);
            }
            else
                nextSong();

        }
    }


    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //System.out.println("position------------"+ms.mp.getCurrentPosition());
            mt = new SimpleDateFormat("mm:ss");
            current.setText(mt.format(ms.mp.getCurrentPosition()));
            total.setText(mt.format(ms.mp.getDuration()));
            seekBar.setProgress(ms.mp.getCurrentPosition());
            seekBar.setMax(ms.mp.getDuration());
//            Log.d("getCurrentPosition", ""+ms.mp.getCurrentPosition());
//            Log.d("getDuration", ""+ms.mp.getDuration());
//            if (Math.abs(ms.mp.getCurrentPosition()-ms.mp.getDuration())<=120){
//                state.setText("已停止");
//                play.setBackground(getResources().getDrawable(R.mipmap.play));
//                ms.stop();
//                seekBar.setProgress(0);
//            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        ms.mp.seekTo(seekBar.getProgress());
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            handler.postDelayed(runnable,100);
        }
    };

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ms = ((MusicService.MyBinder) iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ms = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        seekBar=(SeekBar) findViewById(R.id.seekBar);
        play=(Button) findViewById(R.id.play);
        stop=(Button) findViewById(R.id.stop);
        quit=(Button) findViewById(R.id.quit);
        pre=(Button) findViewById(R.id.pre);
        next=(Button) findViewById(R.id.next);
        state=(TextView) findViewById(R.id.state);
        current=(TextView) findViewById(R.id.current);
        total=(TextView) findViewById(R.id.total);
        t=(TextView) findViewById(R.id.t);
        a=(TextView) findViewById(R.id.a);

        music = new Music();
        infos = music.getInfos(Player.this.getContentResolver());
        size=infos.size();
        u=new String[size];
        title=new String[size];
        artist=new String[size];
        for(int i=0;i<size;i++){
            u[i]= String.valueOf(infos.get(i).getUrl());
            title[i]=String.valueOf(infos.get(i).getTitle());
            artist[i]=String.valueOf(infos.get(i).getArtist());
        }


        final Intent mIntent=getIntent();
        position=mIntent.getIntExtra("position",0);
        Log.d("position in Player", ""+position);

        t.setText(title[position]);
        a.setText(artist[position]);
        url=u[position];

        intent= new Intent(this, MusicService.class);
        Bundle bundle1 = new Bundle();
        bundle1.putString("url", url);
        bundle1.putString("title", title[position]);
        bundle1.putString("artist", artist[position]);
        bundle1.putInt("position", position);
        intent.putExtras(bundle1);
//        intent.putExtra("url",url);
        bindService(intent,sc,BIND_AUTO_CREATE);
        state.setText("正在播放");//初始状态，点击播放列表，马上播放


        ms.mp.setOnCompletionListener(new InnerOnCompletionListener());// MediaPlay自动播放下一首

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ms.play();
                starting=true;
                if (MusicService.mp.isPlaying()) {
                    state.setText("正在播放");
                    play.setBackground(getResources().getDrawable(R.mipmap.pause));
                } else {
                    state.setText("已暂停");
                    play.setBackground(getResources().getDrawable(R.mipmap.play));
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ms.stop();
                seekBar.setProgress(0);
                play.setBackground(getResources().getDrawable(R.mipmap.play));
                state.setText("已停止");
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= 0) {
                    Toast.makeText(Player.this, "再次点击，从最后一首开始播放", Toast.LENGTH_SHORT).show();
                    position=size;
                } else {
                    preSong();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= size-1) {
                    Toast.makeText(Player.this, "再次点击，从第一首开始播放", Toast.LENGTH_SHORT).show();
                    position=-1;
                } else {
                    nextSong();
                }
            }
        });

        final AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        quit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String alert="确定退出应用？";
                alertDialog.setTitle("退出").
                        setMessage(alert).
                        setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.removeCallbacks(runnable);
                                        unbindService(sc);
                                        Intent intent = getIntent();
                                        setResult(1, intent);
                                        finish();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }



    //按返回键1次：返回列表；连续按2次，退出程序。
    //对系统返回键进行监听,定义一个变量记录按键时间,通过计算时间差来实现该功能
//    private long mExitTime;
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
//                mExitTime = System.currentTimeMillis();
//                ms.mp.stop();
//                Player.this.finish();
//                //System.exit(0);
//            } else {
//                handler.removeCallbacks(runnable);
//                unbindService(sc);
//                Intent intent = getIntent();
//                setResult(1, intent);
//                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public void onDestroy() {
        Intent intent = getIntent();
        setResult(0, intent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(ms.mp.isPlaying()) {
            state.setText("正在播放");
        }
        else {
            if(starting){
                state.setText("已暂停");
            }
        }
        handler.post(runnable);
        //seekBar.setProgress(ms.mp.getCurrentPosition());
        //seekBar.setMax(ms.mp.getDuration());
        super.onResume();
    }

    void nextSong(){
        handler.removeCallbacks(runnable);
        unbindService(sc);
        position++;
        t.setText(title[position]);
        a.setText(artist[position]);
        state.setText("正在播放");
        play.setBackground(getResources().getDrawable(R.mipmap.pause));
        url = u[position];
        handler.post(runnable);
        Bundle bundle1 = new Bundle();
        bundle1.putString("url", url);
        bundle1.putString("title", title[position]);
        bundle1.putString("artist", artist[position]);
        bundle1.putInt("position", position);
        intent.putExtras(bundle1);
//        intent.putExtra("url", url);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    void preSong(){
        handler.removeCallbacks(runnable);
        unbindService(sc);
        position--;
        t.setText(title[position]);
        a.setText(artist[position]);
        state.setText("正在播放");
        play.setBackground(getResources().getDrawable(R.mipmap.pause));
        url = u[position];
        handler.post(runnable);
        Bundle bundle1 = new Bundle();
        bundle1.putString("url", url);
        bundle1.putString("title", title[position]);
        bundle1.putString("artist", artist[position]);
        bundle1.putInt("position", position);
        intent.putExtras(bundle1);
//        intent.putExtra("url", url);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    //仅当activity为task根（即首个启动activity）时才生效 这个方法不会改变task中的activity状态，
    // 按下返回键的作用跟按下HOME效果一样；重新点击应用还是回到应用退出前的状态；
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
