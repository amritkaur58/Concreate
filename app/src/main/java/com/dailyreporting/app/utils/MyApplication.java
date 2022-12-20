package com.dailyreporting.app.utils;

import android.app.Application;
import android.content.Context;

import com.bugsee.library.Bugsee;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orm.SugarContext;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        Bugsee.launch(this, "ca55b642-4323-45e1-a895-bdcad0b9bc60");
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        SugarContext.terminate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}