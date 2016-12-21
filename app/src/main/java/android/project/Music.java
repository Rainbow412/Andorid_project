package android.project;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

//获得本地音乐
public class Music {

    public List<Info> getInfos(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Info> infos = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Info info = new Info(); //新建一个歌曲对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
            cursor.moveToNext();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));	//音乐id

            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题

            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家

            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长

            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));	//文件大小

            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));	//文件路径

            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM)); //唱片图片

            long album_id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //唱片图片ID

            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐



            //if (isMusic != 0 && duration/(1000*60)>=1) {     //只把1分钟以上的音乐添加到集合当中
            if (isMusic != 0 && duration>=30000) {  //只把30秒以上的音乐添加到集合当中
                info.setId(id);
                info.setTitle(title);
                info.setArtist(artist);
                info.setDuration(duration);
                info.setSize(size);
                info.setUrl(url);
                info.setAlbum(album);
                info.setAlbum_id(album_id);
                infos.add(info);
            }
        }
        return infos;
    }

//    public void setMusicAdpter(Context context,List<Info> infos,ListView mMusicList) {
//        MusicAdapter mAdapter = new MusicAdapter(context, infos);
//        mMusicList.setAdapter(mAdapter);
//    }
}
