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

    public static void start(String deviceUuid, Boolean send) {
        try {
            Application applicationContext = getApplicationContext();
            applicationContext.registerActivityLifecycleCallbacks(myCallback);

            uuid = deviceUuid;
            sendToApi = send;

            if (send) {
                client = new AndroidKeenClientBuilder(applicationContext).build();
                KeenProject project = new KeenProject(
                        "58f6297895cfc9addc247414",
                        "4AA94B8D6249A0102285093AE900C15C06C261E34B641E0D27376E3FA9EE127E4E004C6FB2871D42C670FC0F19E0EF4576D8D6C77ADC1DB0230F991E7E46244454CF59C71039FAB1FB10CEC1EB0E4004826A33AA22E084A4A4F7E218A39E0F61",
                        null
                );
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

    private static Application getApplicationContext() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Context context;
        final Class<?> activityThreadClass =
                Class.forName("android.app.ActivityThread");
        final Method method = activityThreadClass.getMethod("currentApplication");
        context = (Application) method.invoke(null, (Object[]) null);
        return (Application)context;
    }
}