package com.charjack.charjackplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.charjack.charjackplayer.BaseActivity;
import com.charjack.charjackplayer.R;
import com.charjack.charjackplayer.utils.MediaUtils;
import com.charjack.charjackplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/29.
 */
public class PlayActivity extends BaseActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{

    private TextView textView1_title,textView1_start_time,textView1_end_time;
    private ImageView imageView1_album,imageView1_play_mode,imageView3_previous,imageView2_play_pause,imageView1_next,imageView1_favorite;
    private SeekBar seekBar1;
    private static final int UPDATE_TIME=0X1;
    private ViewPager viewPager;
    private ArrayList<View> views = new ArrayList<>();
//    private View firstpager;
   // private ArrayList<Mp3Info> mp3Infos;

    private JackPlayerApp app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        app = (JackPlayerApp) getApplication();

        /*firstpager =  getLayoutInflater().inflate(R.layout.album_image_layout, null);*/
//        textView1_title = (TextView)firstpager.findViewById(R.id.textView1_title);
        textView1_start_time = (TextView)findViewById(R.id.textView1_start_time);
        textView1_end_time = (TextView)findViewById(R.id.textView1_end_time);

//        imageView1_album = (ImageView) firstpager.findViewById(R.id.imageView1_album);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);
        imageView1_favorite = (ImageView) findViewById(R.id.imageView1_favorite);
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);
        imageView1_next = (ImageView) findViewById(R.id.imageView1_next);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);

        imageView2_play_pause.setOnClickListener(this);
        imageView1_next.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        imageView1_favorite.setOnClickListener(this);
        imageView1_play_mode.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);

        initviewpager();
        //mp3Infos = MediaUtils.getMp3Infos(this);
        myHandler = new MyHandler(this);
    }
    private void initviewpager(){
        views.add(getLayoutInflater().inflate(R.layout.album_image_layout,null));
        views.add(getLayoutInflater().inflate(R.layout.lrc_layout, null));
        viewPager.setAdapter(new MyPagerAdapter());
    }

    //适配器
    class MyPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return views.size();
        }

        //实例化选项卡
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v);
            return v;
        }

        //判断视图是否为返回的对象
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    private static MyHandler myHandler;

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b){
            playService.pause();
            seekBar.setProgress(i);
            playService.seek(i);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    static class MyHandler extends Handler{
        private PlayActivity playActivity;
        public MyHandler(PlayActivity playActivity){
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(playActivity!=null)
            {
                switch(msg.what){
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }


    @Override
    public void publish(int progress) {
     //   textView1_start_time.setText(MediaUtils.formatTime(progress));
        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        seekBar1.setProgress(progress);
        myHandler.sendMessage(msg);
    }

    @Override
    public void change(int position) {

            Mp3Info mp3Info = playService.mp3Infos.get(position);
//            textView1_title.setText(mp3Info.getTittle());

//            System.out.println(textView1_title.getText() + "songalbum---");//名称得到了，但是没有改变

            Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
//            imageView1_album.setImageBitmap(albumBitmap);


            textView1_title = (TextView)(views.get(0).findViewById(R.id.textView1_title));
            textView1_title.setText(mp3Info.getTittle());
            imageView1_album = (ImageView) views.get(0).findViewById(R.id.imageView1_album);
            imageView1_album.setImageBitmap(albumBitmap);




        textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));


            seekBar1.setProgress(0);
            seekBar1.setMax((int)mp3Info.getDuration());

            if(playService.isPlaying()){
                imageView2_play_pause.setImageResource(R.mipmap.pause);
            }else{
                imageView2_play_pause.setImageResource(R.mipmap.play);
            }

            switch(playService.getPlay_mode()){
                case PlayService.ORDER_PLAY:
                    imageView1_play_mode.setImageResource(R.mipmap.order);
                    break;
                case PlayService.RANDOM_PLAY:
                    imageView1_play_mode.setImageResource(R.mipmap.random);
                    break;
                case PlayService.SINGLE_PLAY:
                    imageView1_play_mode.setImageResource(R.mipmap.single);
                    break;
                default:imageView1_play_mode.setImageResource(R.mipmap.order);
                    break;
            }

        try {
            Mp3Info favorMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
            if(favorMp3Info != null){
                imageView1_favorite.setImageResource(R.mipmap.xin_hong);
            }else{
                imageView1_favorite.setImageResource(R.mipmap.xin_bai);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageView2_play_pause:
                if(playService.isPlaying()){
                    playService.pause();
                    imageView2_play_pause.setImageResource(R.mipmap.play);
                }else {
                    if(playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        playService.start();
                    }else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.imageView1_next:
                playService.next();
                break;
            case R.id.imageView3_previous:
                playService.prev();
                break;
            case R.id.imageView1_play_mode:
                switch(playService.getPlay_mode()){
                    case PlayService.ORDER_PLAY:
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        imageView1_play_mode.setImageResource(R.mipmap.random);
                        break;
                    case PlayService.RANDOM_PLAY:
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        imageView1_play_mode.setImageResource(R.mipmap.single);
                        break;
                    case PlayService.SINGLE_PLAY:
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        imageView1_play_mode.setImageResource(R.mipmap.order);
                        break;
                    default:
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        imageView1_play_mode.setImageResource(R.mipmap.random);
                        break;
                }
            case R.id.imageView1_favorite:
                Mp3Info mp3Info = playService.mp3Infos.get(playService.getCurrentPosition());

                try {
                    Mp3Info favorsonginfo =null;
                    favorsonginfo = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getMp3InfoId()));
                    if(favorsonginfo == null){
                        imageView1_favorite.setImageResource(R.mipmap.xin_hong);
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        app.dbUtils.save(mp3Info);
                    }else {
                        imageView1_favorite.setImageResource(R.mipmap.xin_bai);
                        app.dbUtils.deleteById(Mp3Info.class, favorsonginfo.getId());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
            default:break;
        }
    }
}
