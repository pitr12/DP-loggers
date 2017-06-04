package com.example.logger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;

import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.keen.client.android.AndroidKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenLogging;
import io.keen.client.java.KeenProject;

/**
 * Created by peter on 26.9.2016.
 */

public abstract class Logger {
    private static MyActivityLifeCycleCallbacks myCallback = new MyActivityLifeCycleCallbacks();
    public static JSONArray stack = new JSONArray();
    public static MyClickEvent lastClick;
    public static MyClickEvent scrollEvent;
    public static KeenClient client;
    public static String uuid;
    public static Boolean sendToApi;

    public static void setCurrentActivity(Activity activity) {
        final Window win = activity.getWindow();
        final Window.Callback localCallback = win.getCallback();
        if (!(localCallback instanceof MyWindowCallback)) {
            win.setCallback(new MyWindowCallback(localCallback, activity));
        }
    }

    // logger initialization params(
    //  deviceUuid - unique device identifier
    //  send - boolean whether events should be send to remote API (keen.io)
    //  keenProjectID - ID of keen.io project
    //  keenWriteKey - keen project write key
    // )
    public static void start(String deviceUuid, Boolean send, String keenProjectID, String keenWriteKey) {
        try {
            Application applicationContext = getApplicationContext();
            applicationContext.registerActivityLifecycleCallbacks(myCallback);

            uuid = deviceUuid;
            sendToApi = send;

            if (send) {
                client = new AndroidKeenClientBuilder(applicationContext).build();
                KeenProject project = new KeenProject(keenProjectID, keenWriteKey, null);
                client.setDefaultProject(project);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // ability to automatically detect application context - used to get current running Activity
    private static Application getApplicationContext() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Context context;
        final Class<?> activityThreadClass =
                Class.forName("android.app.ActivityThread");
        final Method method = activityThreadClass.getMethod("currentApplication");
        context = (Application) method.invoke(null, (Object[]) null);
        return (Application)context;
    }
}