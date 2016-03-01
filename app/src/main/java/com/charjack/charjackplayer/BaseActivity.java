package com.charjack.charjackplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.charjack.charjackplayer.utils.Content;

/**
 * Created by Administrator on 2016/2/29.
 */
public abstract class BaseActivity extends FragmentActivity {

    public PlayService playService;
    public boolean isbound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();

            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playService =null;
        }
    };

    public void bindPlayService(){
        if(isbound == false)
        {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isbound = true;
        }
    }


    public void unbindPlayService(){
        if(isbound==true)
        {
            unbindService(conn);
            isbound = false;
        }
    }


    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener(){
        @Override
        public void onPublic(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };


    public abstract void publish(int progress);
    public abstract void change(int position);


}

