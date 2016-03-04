package com.charjack.charjackplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.charjack.charjackplayer.MyMusicListFragment;
import com.charjack.charjackplayer.PlayActivity;
import com.charjack.charjackplayer.R;
import com.charjack.charjackplayer.utils.MediaUtils;
import com.charjack.charjackplayer.vo.Mp3Info;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/29.
 */
public class MyMusicListAdapter extends BaseAdapter{

    private Context ctx;
    ArrayList<Mp3Info> mp3Infos;

    public MyMusicListAdapter(Context ctx, ArrayList<Mp3Info> mp3Infos){
        this.ctx = ctx;
        this.mp3Infos = mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int i) {
        return mp3Infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        ViewHolder vh;
        if(convertview == null){
            convertview = LayoutInflater.from(ctx).inflate(R.layout.item_music_list,null);
            vh = new ViewHolder();
            vh.imageView1_icon = (ImageView) convertview.findViewById(R.id.imageView1_icon);
            vh.textView1_title = (TextView) convertview.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertview.findViewById(R.id.textView2_singer);
            vh.textView3_time = (TextView) convertview.findViewById(R.id.textView3_time);

            convertview.setTag(vh);
        }

        vh = (ViewHolder) convertview.getTag();
        Mp3Info mp3Info = mp3Infos.get(i);
        vh.textView1_title.setText(mp3Info.getTittle());
        vh.textView2_singer.setText(mp3Info.getArtist());
        vh.textView3_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));

//        vh.imageView1_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ctx.startActivity(new Intent(ctx, PlayActivity.class));
//
//            }
//        });
        return convertview;
    }


    private class ViewHolder{
        ImageView imageView1_icon;
        TextView textView1_title;
        TextView textView2_singer;
        TextView textView3_time;

    }

    public interface iconplayinf{
        public void iconplay(int position);
    }
}
