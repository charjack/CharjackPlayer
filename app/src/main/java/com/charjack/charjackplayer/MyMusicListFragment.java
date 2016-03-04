package com.charjack.charjackplayer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.charjack.charjackplayer.adapter.MyMusicListAdapter;
import com.charjack.charjackplayer.utils.MediaUtils;
import com.charjack.charjackplayer.vo.Mp3Info;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyMusicListFragment extends Fragment implements View.OnClickListener{

    private ListView listView_my_music;
    private ArrayList<Mp3Info> mp3Infos;

    private ImageView imageView_album;
    private TextView textView_songName,textView2_singer;
    private ImageView imageView2_play_pause,imageView3_next;
    private MainActivity mainActivity;

    private int position = 0;
    private MyMusicListAdapter myMusicListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        return my;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);//布局文件写错，导致一直报空指针
        listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);

        imageView_album = (ImageView) view.findViewById(R.id.imageView_album);
        imageView2_play_pause = (ImageView) view.findViewById(R.id.imageView2_play_pause);
        imageView3_next = (ImageView) view.findViewById(R.id.imageView3_next);

        textView_songName = (TextView) view.findViewById(R.id.textView_songName);
        textView2_singer = (TextView) view.findViewById(R.id.textView2_singer);

        imageView2_play_pause.setOnClickListener(this);
        imageView3_next.setOnClickListener(this);
        listView_my_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("-------enter itemclick--");
                mainActivity.playService.ChangePlayList = mainActivity.playService.MY_MUSIC_LIST;
                mainActivity.playService.setMp3Infos(mp3Infos);
                mainActivity.playService.play(i);
            }
        });
        imageView_album.setOnClickListener(this);
        textView_songName.setOnClickListener(this);
        textView2_singer.setOnClickListener(this);
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();
    }

    private void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        myMusicListAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.unbindPlayService();
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//        System.out.println("enter itemclick");
//        mainActivity.playService.ChangePlayList = mainActivity.playService.MY_MUSIC_LIST;
//        mainActivity.playService.setMp3Infos(mp3Infos);
//        mainActivity.playService.play(i);
//    }



    public void changeUIStatusOnPlay(int position){
        if(position>=0&&position<mainActivity.playService.mp3Infos.size()){
            Mp3Info mp3Info = mainActivity.playService.mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTittle());
            textView2_singer.setText(mp3Info.getArtist());
            if(mainActivity.playService.isPlaying()){
                imageView2_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
            }else{
                imageView2_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
            }

            System.out.println("songid"+mp3Info.getId()+"albumid"+mp3Info.getAlbumId());
            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView_album.setImageBitmap(albumBitmap);
            this.position = position;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView2_play_pause:
               if(mainActivity.playService.isPlaying()){
                   imageView2_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
                   mainActivity.playService.pause();
               }else {
                   if(mainActivity.playService.isPause()) {
                       imageView2_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                       mainActivity.playService.start();
                   }else {
                       mainActivity.playService.play(position);
                   }
               }
                break;
            case R.id.imageView3_next:
                mainActivity.playService.next();
                break;
            case R.id.imageView_album:
                startActivity(new Intent(mainActivity, PlayActivity.class));
                break;
            case R.id.textView2_singer:
                startActivity(new Intent(mainActivity, PlayActivity.class));
                break;
            case R.id.textView_songName:
                startActivity(new Intent(mainActivity, PlayActivity.class));
                break;

        }

    }


}
