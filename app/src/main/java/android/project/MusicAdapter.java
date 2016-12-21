package android.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

class ViewContainer{
    public TextView title;
    public TextView artist;
    public TextView duration;


}

public class MusicAdapter extends BaseAdapter {

    private Context context;        //上下文对象引用
    private List<Info> infos;   //存放Mp3Info引用的集合
    private Info info;        //Mp3Info对象引用
    private int pos = -1;           //列表位置
    private ViewContainer vc;
    private SimpleDateFormat mt;


    public MusicAdapter(Context context,List<Info> mp3Infos){
        this.context = context;
        this.infos = mp3Infos;
    }

    public void refresh(List<Info> newInfos){
        this.infos = newInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        vc = null;
        if(convertView == null){
            vc = new ViewContainer();
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            vc.title = (TextView)convertView.findViewById(R.id.title);
            vc.artist = (TextView)convertView.findViewById(R.id.artist);
            vc.duration = (TextView)convertView.findViewById(R.id.duration);
            convertView.setTag(vc);
        }
        else{
            vc = (ViewContainer)convertView.getTag();
        }
        info = infos.get(position);
        vc.title.setText(info.getTitle());         //显示标题
        vc.artist.setText(info.getArtist());       //显示艺术家
        mt = new SimpleDateFormat("mm:ss");
        vc.duration.setText(mt.format(info.getDuration()));  //显示时长
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

}
