package com.wyh.opengl;

import android.app.Application;

/**
 * @author WangYingHao
 * @since 2019-06-09
 */
public class App extends Application {
    private static App instance;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
