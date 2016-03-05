package com.charjack.charjackplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charjack.charjackplayer.BaseActivity;
import com.charjack.charjackplayer.R;
import com.charjack.charjackplayer.vo.SearchResult;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/4.
 */
public class NetMusicAdapter extends BaseAdapter{

    private Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private ArrayList<SearchResult> searchResults;

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }


    public NetMusicAdapter(Context ctx,ArrayList<SearchResult> searchResults){
        this.ctx = ctx;
        this.searchResults =searchResults;
    }


    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int i) {
        return searchResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if(convertview == null){
            vh = new ViewHolder();
            convertview = LayoutInflater.from(ctx).inflate(R.layout.net_item_musci_list,null);

            vh.textView1_title = (TextView) convertview.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertview.findViewById(R.id.textView2_singer);
            convertview.setTag(vh);
        }

        vh = (ViewHolder) convertview.getTag();

        vh.textView1_title.setText(searchResults.get(i).getMusciName());
        vh.textView2_singer.setText(searchResults.get(i).getArtist());
        return convertview;
    }

    private class ViewHolder{
        TextView textView1_title;
        TextView textView2_singer;
    }
}
