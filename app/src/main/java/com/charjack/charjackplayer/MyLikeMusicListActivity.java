package com.charjack.charjackplayer;

import android.os.Bundle;
import android.widget.ListView;

import com.charjack.charjackplayer.adapter.MyMusicListAdapter;
import com.charjack.charjackplayer.vo.Mp3Info;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/1.
 */
public class MyLikeMusicListActivity extends BaseActivity {


    private ListView listView_like;
    private ArrayList<Mp3Info> likemp3Infos;
    private JackPlayerApp app;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_music_list);

        likemp3Infos = new ArrayList<>();
        app = (JackPlayerApp) getApplication();

        initdata();
        listView_like = (ListView) findViewById(R.id.listView_like);
        listView_like.setAdapter(new MyMusicListAdapter(this,likemp3Infos));
    }

    private void initdata() {
        try {
            likemp3Infos = (ArrayList<Mp3Info>) app.dbUtils.findAll(Mp3Info.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
}
