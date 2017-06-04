package com.example.logger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by peter on 5.10.2016.
 */

public class MyActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Logger.setCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logger.setCurrentActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logger.setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
