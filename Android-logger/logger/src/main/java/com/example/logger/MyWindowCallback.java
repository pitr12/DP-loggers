package com.example.logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by peter on 28.9.2016.
 */

public class MyWindowCallback implements Window.Callback{

    Window.Callback localCallback;
    Activity context;
    int x;
    int y;
    int absX;
    int absY;
    int offsetX;
    int offsetY;
    String ts;
    String screen;
    int lastEventType;

    // windows callback initialization
    public MyWindowCallback(Window.Callback localCallback, Activity context) {
        this.localCallback = localCallback;
        this.context = context;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return localCallback.dispatchKeyEvent(event);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return localCallback.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // tracks touch and scroll events
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            //Log.d("logger", event.toString()); - used to debugging

            this.lastEventType = event.getAction();

            // detection of scroll event
            if (this.lastEventType == MotionEvent.ACTION_MOVE) {
                if (Logger.scrollEvent == null) {
                    Logger.scrollEvent = new MyClickEvent("Scroll", "Unknown", Logger.lastClick.getX(), Logger.lastClick.getY(), Logger.lastClick.getAbsX(), Logger.lastClick.getAbsY(), Logger.lastClick.getEventScreen(), Logger.lastClick.getTimestamp());
                }
            }

            // find current view
            View view = context.findViewById(android.R.id.content);

            //get event coordinates
            int[] screenLocation = new int[2];
            view.getLocationOnScreen(screenLocation);
            Float x = event.getRawX() - screenLocation[0];
            Float y = event.getRawY() - screenLocation[1];
            TimeZone tz = TimeZone.getTimeZone("UTC");
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            this.ts = df.format(new Date());
            this.screen = context.getClass().getSimpleName();
            this.x = Math.round(x);
            this.y = Math.round(y);
            this.absX = Math.round(event.getRawX());
            this.absY = Math.round(event.getRawY());

            // detect clicked element
            View target = getClickedView(view, Math.round(x), Math.round(y));

             //no specific target detected
            if (target == null) {
                Logger.lastClick = new MyClickEvent("Raw", "Unknown", this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, this.screen, this.ts);
            }else {
//                Log.d("logger", Logger.lastClick.stringify());
            }
        // send click event to remote API
        }else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (this.lastEventType == MotionEvent.ACTION_DOWN) {
                if(Logger.sendToApi) {
                    Map<String, Object> currentLog = new HashMap<String, Object>();
                    currentLog.put("type", Logger.lastClick.getType());
                    currentLog.put("label", Logger.lastClick.getLabel());
                    currentLog.put("x", Logger.lastClick.getX());
                    currentLog.put("y", Logger.lastClick.getY());
                    currentLog.put("toX", Logger.lastClick.getToX());
                    currentLog.put("toY", Logger.lastClick.getToY());
                    currentLog.put("xAbsolute", Logger.lastClick.getxAbsolute());
                    currentLog.put("yAbsolute", Logger.lastClick.getyAbsolute());
                    currentLog.put("toXAbsolute", Logger.lastClick.getToXAbsolute());
                    currentLog.put("toYAbsolute", Logger.lastClick.getToYAbsolute());
                    currentLog.put("screen", Logger.lastClick.getScreen());
                    currentLog.put("timestamp", Logger.lastClick.getTimestamp());
                    currentLog.put("uuid", Logger.uuid);
                    Logger.client.addEvent(Logger.uuid, currentLog);
                }
            // send scroll event to remote API
            }else if (this.lastEventType == MotionEvent.ACTION_MOVE && Logger.scrollEvent != null) {
                Logger.scrollEvent.setScrollCoordinates(Logger.lastClick.getX(), Logger.lastClick.getY(), Logger.lastClick.getAbsX(), Logger.lastClick.getAbsY());
                if(Logger.sendToApi) {
                    Map<String, Object> currentLog = new HashMap<String, Object>();
                    currentLog.put("type", Logger.scrollEvent.getType());
                    currentLog.put("label", Logger.scrollEvent.getLabel());
                    currentLog.put("x", Logger.scrollEvent.getX());
                    currentLog.put("y", Logger.scrollEvent.getY());
                    currentLog.put("toX", Logger.scrollEvent.getToX());
                    currentLog.put("toY", Logger.scrollEvent.getToY());
                    currentLog.put("xAbsolute", Logger.scrollEvent.getxAbsolute());
                    currentLog.put("yAbsolute", Logger.scrollEvent.getyAbsolute());
                    currentLog.put("toXAbsolute", Logger.scrollEvent.getToXAbsolute());
                    currentLog.put("toYAbsolute", Logger.scrollEvent.getToYAbsolute());
                    currentLog.put("screen", Logger.scrollEvent.getScreen());
                    currentLog.put("timestamp", Logger.scrollEvent.getTimestamp());
                    currentLog.put("uuid", Logger.uuid);
                    Logger.client.addEvent(Logger.uuid, currentLog);
                    Logger.scrollEvent = null;
                }
            }

            Logger.lastClick = null;

        }

        return localCallback.dispatchTouchEvent(event);
    }

    // recursively detect clicked element
    private View getClickedView(View view, int x, int y) {
        View target = null;

        for (int _numChildren = ((ViewGroup) view).getChildCount(); _numChildren > 0; --_numChildren) {
            View _child = ((ViewGroup) view).getChildAt(_numChildren - 1);
            Rect _bounds = new Rect();
            _child.getHitRect(_bounds);
            if (_bounds.contains(x, y)) {
                if (_child != null) {
//                    Log.d("logger", _child.getClass().getSimpleName());
                    switch(_child.getClass().getSimpleName()) {
                        case "ScrollView":
                            this.offsetY = _child.getScrollY();
                            this.offsetX = _child.getScrollX();
                            target = getClickedView(_child, x - _bounds.left, y - _bounds.top);
                            break;
                        case "AppCompatButton":
                            target = _child;
                            Logger.lastClick = new MyClickEvent("Button", "" + (((android.widget.TextView)target).getText()), this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, context.getClass().getSimpleName(), this.ts);
                            break;
                        case "AppCompatTextView":
                            target = _child;
                            Logger.lastClick = new MyClickEvent("TextView", "" + (((android.widget.TextView)target).getText()), this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, context.getClass().getSimpleName(), this.ts);
                            break;
                        case "AppCompatCheckBox":
                            target = _child;
                            Logger.lastClick = new MyClickEvent("Checkbox", "" + (((android.widget.TextView)target).getText()), this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, context.getClass().getSimpleName(), this.ts);
                            break;
                        case "AppCompatRadioButton":
                            target = _child;
                            Logger.lastClick = new MyClickEvent("RadioButton", "" + (((android.widget.TextView)target).getText()), this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, context.getClass().getSimpleName(), this.ts);
                            break;
                        default:
                            if (_child instanceof android.view.ViewGroup) {
                                target = getClickedView(_child, x - _bounds.left, y - _bounds.top);
                            }else {
                                target = _child;
                                Logger.lastClick =  new MyClickEvent("Element", "Unknown", this.x + this.offsetX, this.y + this.offsetY, this.absX, this.absY, this.screen, this.ts);
                            }
                    }
                }
            }
        }

        return target;
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return localCallback.dispatchTrackballEvent(event);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return localCallback.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return localCallback.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return localCallback.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return localCallback.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        boolean ret = localCallback.onPreparePanel(featureId, view, menu);
        return ret;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return localCallback.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return localCallback.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        localCallback.onWindowAttributesChanged(attrs);
    }

    @Override
    public void onContentChanged() {
        localCallback.onContentChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        Log.d("","ttest onfocus changed called");
        localCallback.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        localCallback.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        localCallback.onDetachedFromWindow();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        localCallback.onPanelClosed(featureId, menu);
    }

    @Override
    public boolean onSearchRequested() {
        return localCallback.onSearchRequested();
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return localCallback.onWindowStartingActionMode(callback);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    public void onActionModeStarted(ActionMode mode) {
        localCallback.onActionModeStarted(mode);

    }

    @SuppressLint("NewApi")
    @Override
    public void onActionModeFinished(ActionMode mode) {
        localCallback.onActionModeFinished(mode);

    }
}
