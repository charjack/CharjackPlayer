package com.charjack.charjackplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.charjack.charjackplayer.JackPlayerApp;

/**
 * Created by Administrator on 2016/3/4.
 */
public class AppUtils {

    public static void hideInputMethod(View view){
        InputMethodManager imm = (InputMethodManager) JackPlayerApp.context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static void showInputMethod(View view){
        InputMethodManager imm = (InputMethodManager) JackPlayerApp.context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive() == false) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            System.out.println("going to open");
        }
    }
}
