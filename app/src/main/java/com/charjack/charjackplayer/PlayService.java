package com.charjack.charjackplayer;

import android.app.Service;
import android.content.Intent;
import android.drm.DrmErrorEvent;
import android.drm.DrmManagerClient;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.charjack.charjackplayer.utils.MediaUtils;
import com.charjack.charjackplayer.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{

    private MediaPlayer mPlayer;
    private int currentPosition;
    ArrayList<Mp3Info>  mp3Infos;
    private boolean isPause = false;

    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    public int play_mode = ORDER_PLAY;


    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;


    }

    public int getPlay_mode() {
        return play_mode;
    }

    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();

    public PlayService() {
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    private Random random =new Random();
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch(play_mode){
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size()));
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }


    class PlayBinder extends Binder{
        public PlayService getPlayService(){
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JackPlayerApp app = (JackPlayerApp) getApplication();
        currentPosition = app.sp.getInt("currentPosition",0);
        play_mode = app.sp.getInt("play_mode",PlayService.ORDER_PLAY);

        mPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        es.execute(updateStatusRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(es!=null&& !es.isShutdown()){
            es.shutdown();
            es=null;
        }
    }

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                if(musicUpdateListener!=null &&mPlayer!=null&& mPlayer.isPlaying()){
                    musicUpdateListener.onPublic(getcurrentProgress());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    public void play(int position){
        if(position>=0&&position< mp3Infos.size()){
            try {
                mPlayer.reset();
                mPlayer.setDataSource(this, Uri.parse(mp3Infos.get(position).getUrl()));
                mPlayer.prepare();
                mPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(musicUpdateListener!=null){
                musicUpdateListener.onChange(currentPosition);
            }
            isPause = false;
        }
    }

    public boolean isPlaying(){
        if(mPlayer !=null){
            return mPlayer.isPlaying();
        }
        return false;
    }

    public void pause(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            isPause = true;
        }
    }

    public boolean isPause(){return isPause;}

    public void next(){
        if(currentPosition+1 >= mp3Infos.size()){
            currentPosition = 0;
        }else
        {
            currentPosition++;
        }
        play(currentPosition);
    }

    public void prev(){
        if(currentPosition-1 < 0){
            currentPosition = mp3Infos.size()-1;
        }else
        {
            currentPosition--;
        }
        play(currentPosition);
    }


    public void start(){
        if(mPlayer!=null && (!mPlayer.isPlaying())){
            mPlayer.start();//继续播放
            isPause = false;
        }
    }

    public int getcurrentProgress(){
        if(mPlayer!=null && mPlayer.isPlaying()){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    public int getDuration(){
        return mPlayer.getDuration();
    }

    public void seek (int msec){
        mPlayer.seekTo(msec);
    }

    public interface MusicUpdateListener{
        public void onPublic(int progress);
        public void onChange(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener){
        this.musicUpdateListener = musicUpdateListener;
    }
}
