package android.project;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Music music;
    private List<Info> infos;
    private MusicAdapter adapter;

    // Context menu
    private static final int CMD_EDIT = 4;
    private static final int CMD_DELETE = 5;

    //启动页面，进行功能说明
    private ViewFlipper allFlipper;
    private Handler handler = new Handler() {
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


        /*int size=infos.size();
        final String u[]=new String[size];
        final String title[]=new String[size];
        final String artist[]=new String[size];
        for(int i=0;i<size;i++){
            u[i]= String.valueOf(infos.get(i).getUrl());
            title[i]=String.valueOf(infos.get(i).getTitle());
            artist[i]=String.valueOf(infos.get(i).getArtist());
        }*/



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = infos.get(position);
                //System.out.println("position----------"+position);
                Intent intent = new Intent(MainActivity.this, Player.class);
                intent.putExtra("position",position);
                /*intent.putExtra("title", info.getTitle());
                intent.putExtra("artist", info.getArtist());
                intent.putExtra("url", info.getUrl());
                intent.putExtra("title", title[position]);
                intent.putExtra("artist", artist[position]);
                intent.putExtra("url", u[position]);*/



                /*int preposition=position-1;
                Info preinfo = infos.get(preposition);
                System.out.println("preposition----------"+preposition);
                intent.putExtra("pretitle", preinfo.getTitle());
                intent.putExtra("preartist", preinfo.getArtist());
                intent.putExtra("preurl", preinfo.getUrl());*/

//                startActivity(intent);
                startActivityForResult(intent, 1);

            }
        });

        //长按菜单
        registerForContextMenu(listView);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //按下退出键时关闭应用
        if(resultCode==1){
            try{
                MainActivity.this.finish(); //结束MainActivity
                System.exit(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

//        AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        Info info = infos.get(minfo.position);
//
//        String title = info.getTitle();
//
//        menu.setHeaderTitle(title);

        menu.add(0, CMD_EDIT, 0, "剪辑铃声");
        menu.add(0, CMD_DELETE, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo minfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Info info = infos.get(minfo.position);
        String url = info.getUrl();

        switch (item.getItemId()) {
            case CMD_EDIT:
                startRingdroidEditor(url);
                return true;
            case CMD_DELETE:
                confirmDelete(url);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void startRingdroidEditor(String url) {
        try {
            Intent intent = new Intent(MainActivity.this, RingtoneEditActivity.class);
            intent.putExtra("url", url);

            startActivity(intent);
        } catch (Exception e) {
            Log.d("flag", "Couldn't start editor");
        }
    }

    private void confirmDelete(final String url) {

        new AlertDialog.Builder(MainActivity.this)
//                .setTitle("删除")
                .setMessage("是否删除音乐？")
                .setPositiveButton(
                        "删除",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                onDelete(url);
                            }
                        })
                .setNegativeButton(
                        "取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .setCancelable(true)
                .show();
    }

    private void onDelete(String url) {
        File musicfile = new File(url);
        if(musicfile.isFile())
            musicfile.delete();
    }



}