package com.charjack.charjackplayer;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.charjack.charjackplayer.R;
import com.charjack.charjackplayer.adapter.NetMusicAdapter;
import com.charjack.charjackplayer.utils.AppUtils;
import com.charjack.charjackplayer.utils.Content;
import com.charjack.charjackplayer.utils.SearchMusicUtils;
import com.charjack.charjackplayer.vo.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetMusicListFragment extends Fragment implements ListView.OnItemClickListener,View.OnClickListener {


    private MainActivity mainActivity;
    private ListView listView_net_music;
    private LinearLayout load_layout;
    private LinearLayout ll_search_container;
    private LinearLayout ll_search_btn_container;
    private ImageButton ib_search_btn;
    private EditText et_search_content;
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private NetMusicAdapter netMusicAdapter;
    private  int page =1;

    public static NetMusicListFragment newInstance() {
        // Required empty public constructor
        NetMusicListFragment net = new NetMusicListFragment();
        return net;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.net_music_list,null);

        listView_net_music = (ListView) view.findViewById(R.id.listView_net_music);
        load_layout = (LinearLayout) view.findViewById(R.id.load_layout);
        ll_search_container = (LinearLayout) view.findViewById(R.id.ll_search_container);
        ll_search_btn_container = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
        ib_search_btn = (ImageButton) view.findViewById(R.id.ib_search_btn);
        et_search_content = (EditText) view.findViewById(R.id.et_search_content);

        listView_net_music.setOnItemClickListener(this);
        ll_search_btn_container.setOnClickListener(this);
        ib_search_btn.setOnClickListener(this);
        loadNetData();

        return view;
    }

    private void loadNetData() {
        load_layout.setVisibility(View.VISIBLE);
        new LoadNetDataTask().execute(Content.BAIDU_URL + Content.BAIDU_DAYHOT);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_search_btn_container:
                ll_search_btn_container.setVisibility(View.GONE);
                ll_search_container.setVisibility(View.VISIBLE);
                et_search_content.setFocusable(true);
                et_search_content.setFocusableInTouchMode(true);

                AppUtils.showInputMethod(et_search_content);
                break;
            case R.id.ib_search_btn:
                searchMusic();
                break;
        }
    }

    private void searchMusic() {
        AppUtils.hideInputMethod(et_search_content);
        ll_search_btn_container.setVisibility(View.VISIBLE);
        ll_search_container.setVisibility(View.GONE);
        String key = et_search_content.getText().toString();
        if(TextUtils.isEmpty(key)){
            Toast.makeText(mainActivity,"请输入歌名",Toast.LENGTH_SHORT).show();
            return;
        }

        load_layout.setVisibility(View.VISIBLE);

        SearchMusicUtils.getInstance().setListener(
                new SearchMusicUtils.OnSearchResultListener(){
                    @Override
                    public void onSearchResult(ArrayList<SearchResult> results) {
                        ArrayList<SearchResult> sr = netMusicAdapter.getSearchResults();
                        sr.clear();
                        sr.addAll(results);
                        netMusicAdapter.notifyDataSetChanged();
                        load_layout.setVisibility(View.GONE);

                    }
                }).search(key,page);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i>=netMusicAdapter.getSearchResults().size() || i<0){return;}
        showDownloadDialog(i);
    }


   class LoadNetDataTask extends AsyncTask<String,Integer,Integer> {

       @Override
       protected void onPreExecute() {
           super.onPreExecute();

           load_layout.setVisibility(View.VISIBLE);
           listView_net_music.setVisibility(View.GONE);
           searchResults.clear();
       }

       @Override
       protected Integer doInBackground(String... params) {

           String url = params[0];

           Document doc = null;
           try {
               doc = Jsoup.connect(url).userAgent(Content.USER_AGENT).timeout(6*1000).get();

               System.out.println(doc);
               Elements songTitles = doc.select("span.song-title");
               Elements artists = doc.select("span.author_list");
               for(int i=0;i<songTitles.size();i++){
                   SearchResult searchResult = new SearchResult();
                   Elements urls = songTitles.get(i).getElementsByTag("a");
                   searchResult.setUrl(urls.get(0).attr("href"));
                   searchResult.setMusciName(urls.get(0).text());

                   Elements artistElements = artists.get(i).getElementsByTag("a");
                   searchResult.setArtist(artistElements.get(0).text());

                   searchResult.setAlbum("热歌榜");
                   searchResults.add(searchResult);
               }

           } catch (IOException e) {
               e.printStackTrace();
               return  -1;
           }
           return 1;
       }

       @Override
       protected void onPostExecute(Integer integer) {
           super.onPostExecute(integer);

           if(integer ==1){
               netMusicAdapter = new NetMusicAdapter(mainActivity,searchResults);

               listView_net_music.setAdapter(netMusicAdapter);
               listView_net_music.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.footview_layout,null));

               load_layout.setVisibility(View.GONE);
               listView_net_music.setVisibility(View.VISIBLE);
           }


       }
   }
}
