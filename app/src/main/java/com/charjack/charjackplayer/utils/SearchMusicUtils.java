package com.charjack.charjackplayer.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.charjack.charjackplayer.vo.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/3/5.
 */
public class SearchMusicUtils {

    private static final int SIZE = 20;
    private static final String URL = Content.BAIDU_URL + Content.BAIDU_SEARCH;
    private static SearchMusicUtils sInstance;
    private OnSearchResultListener mListener;

    private ExecutorService mThreadPool;

    public synchronized static SearchMusicUtils getInstance() {
        if (sInstance == null) {
            sInstance = new SearchMusicUtils();
        }
        return sInstance;
    }

    private SearchMusicUtils(){
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public SearchMusicUtils setListener(OnSearchResultListener l){
        mListener = l;
        return this;
    }

    public void search(final String key,final int page){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                            if(mListener !=null)
                                mListener.onSearchResult((ArrayList<SearchResult>)msg.obj);
                        break;
                    case 2:
                        if(mListener!=null) mListener.onSearchResult(null);
                        break;
                }
            }
        };


        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<SearchResult> results = getMusicList(key,page);
                if(results ==null){
                    handler.sendEmptyMessage(2);
                    return;
                }
                handler.obtainMessage(1,results).sendToTarget();
            }
        });
    }

        //使用jsoup请求网络解析数据
    private ArrayList<SearchResult> getMusicList(final String key ,final int page){
        final String start = String.valueOf((page-1)*SIZE);

        try {
            Document doc = Jsoup.connect(URL)
                    .data("key",key,"start",start,"size",String.valueOf(SIZE))
                    .userAgent(Content.USER_AGENT)
                    .timeout(60*1000).get();

//            System.out.println(doc);
            Elements songTitles = doc.select("div.song-item.clearfix");
            Elements songinfos;
            ArrayList<SearchResult> searchResults  = new ArrayList<>();

            TAG:
                for(Element song:songTitles){
                    songinfos = song.getElementsByTag("a");
                    SearchResult searchResult = new SearchResult();
                    for(Element info: songinfos){
                        if(info.attr("href").startsWith("http://y.baidu.com/song/"))//收费歌曲
                            continue TAG;

                        //跳转到百度音乐盒子的歌曲
                        if(info.attr("href").equals("#") && !TextUtils.isEmpty(info.attr("data-songdata"))) {
                            continue TAG;
                        }

                        //歌曲
                        if(info.attr("href").startsWith("/song")){
                            searchResult.setMusciName(info.text());
                            searchResult.setUrl(info.attr("href"));
                        }
                        //歌手
                        if(info.attr("href").startsWith("/data")){
                            searchResult.setArtist(info.text());
                        }

                        //专辑
                        if(info.attr("href").startsWith("/album")){
                            searchResult.setAlbum(info.text().replace("<|》", ""));
                        }
                    }
                    searchResults.add(searchResult);

                }
//            System.out.println(searchResults);
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface OnSearchResultListener{
        public void onSearchResult(ArrayList<SearchResult> results);
    }
}

