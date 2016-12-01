package android.project;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;

public class Player extends AppCompatActivity {
    private Button play;
    private Button stop;
    private Button quit;
    private MusicService ms;
    private TextView state;
    private TextView current;
    private TextView total;
    private SeekBar seekBar;
    private SimpleDateFormat mt;

    boolean starting=false;
    private TextView t;
    private TextView a;
    private String url;

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mt = new SimpleDateFormat("mm:ss");
            current.setText(mt.format(ms.mp.getCurrentPosition()));
            total.setText(mt.format(ms.mp.getDuration()));
            seekBar.setProgress(ms.mp.getCurrentPosition());
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
        state=(TextView) findViewById(R.id.state);
        current=(TextView) findViewById(R.id.current);
        total=(TextView) findViewById(R.id.total);

        Intent mIntent=getIntent();
        t=(TextView) findViewById(R.id.t);
        a=(TextView) findViewById(R.id.a);

        t.setText(mIntent.getStringExtra("title"));
        a.setText(mIntent.getStringExtra("artist"));
        url=mIntent.getStringExtra("url");
        //System.out.println("activity---------------"+url);

        ms=new MusicService();
        Intent intent= new Intent(this, MusicService.class);
        intent.putExtra("url",url);
        bindService(intent,sc,BIND_AUTO_CREATE);

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ms.play();
                starting=true;
                if (MusicService.mp.isPlaying()) {
                    state.setText("Playing");
                    play.setBackground(getResources().getDrawable(R.mipmap.pause));
                } else {
                    state.setText("Paused");
                    play.setBackground(getResources().getDrawable(R.mipmap.play));
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ms.stop();
                seekBar.setProgress(0);
                play.setBackground(getResources().getDrawable(R.mipmap.play));
                state.setText("Stopped");
            }
        });
        quit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
                unbindService(sc);
                try {
                    Player.this.finish();
                    System.exit(0);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }



    public void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(ms.mp.isPlaying()) {
            state.setText("Playing");
        }
        else {
            if(starting){
                state.setText("Paused");
            }
        }
        seekBar.setProgress(ms.mp.getCurrentPosition());
        seekBar.setMax(ms.mp.getDuration());
        handler.post(runnable);
        super.onResume();
    }
}
