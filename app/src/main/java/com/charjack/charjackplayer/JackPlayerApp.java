package com.charjack.charjackplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.charjack.charjackplayer.utils.Content;
import com.lidroid.xutils.DbUtils;

/**
 * Created by Administrator on 2016/3/1.
 */
public class JackPlayerApp extends Application {

    public static SharedPreferences sp;
    public static DbUtils dbUtils;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Content.SP_NAME, Context.MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(),Content.DB_NAME);
        context = getApplicationContext();
    }
}
