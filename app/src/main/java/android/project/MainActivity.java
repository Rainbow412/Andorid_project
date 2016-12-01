package android.project;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Music music;
    private List<Info> infos;
    private MusicAdapter adapter;

    //启动页面，进行功能说明
    private ViewFlipper allFlipper;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //切换到主页面
                    allFlipper.setDisplayedChild(1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动页面，进行功能说明
        allFlipper = (ViewFlipper) findViewById(R.id.allFlipper);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1); //给UI主线程发送消息
            }
        }, 2000); //启动等待2秒钟
        listView = (ListView) findViewById(R.id.listview);
        music = new Music();
        infos = music.getInfos(MainActivity.this.getContentResolver());
        adapter = new MusicAdapter(getApplicationContext(), infos);
        music.setMusicAdpter(getApplicationContext(), infos, listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info=infos.get(position);
                Intent intent= new Intent(MainActivity.this,Player.class);
                intent.putExtra("title", info.getTitle());
                intent.putExtra("artist", info.getArtist());
                intent.putExtra("url",info.getUrl());

                startActivity(intent);
            }
        });


    }
}