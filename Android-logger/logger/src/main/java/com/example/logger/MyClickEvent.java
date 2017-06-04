package com.example.logger;

import java.sql.Time;
import java.util.Date;

/**
 * Created by peter on 12.10.2016.
 * representation of Event send to remote API
 */

public class MyClickEvent {
    private String type;
    private String label;
    private int x;
    private int y;
    private int toX;
    private int toY;
    private int xAbsolute;
    private int yAbsolute;
    private int toXAbsolute;
    private int toYAbsolute;
    private String screen;
    private String timestamp;

    public MyClickEvent (String type, String label, int x, int y, int xAbs, int yAbs, String screen, String timestamp) {
        this.type = type;
        this.label = label;
        this.x = x;
        this.y = y;
        this.screen = screen;
        this.timestamp = timestamp;
        this.toX = -1;
        this.toY = -1;
        this.toXAbsolute = -1;
        this.toYAbsolute = -1;
        this.xAbsolute = xAbs;
        this.yAbsolute = yAbs;
    }

    public void setScrollCoordinates(int x, int y, int xAbs, int yAbs) {
        this.toX = x;
        this.toY = y;
        this.toXAbsolute = xAbs;
        this.toYAbsolute = yAbs;
    }

    public int getX() {
        return this.x;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getEventScreen() {
        return this.screen;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getY() {
        return this.y;
    }

    public int getAbsX() {
        return this.xAbsolute;
    }

    public int getAbsY() {
        return this.yAbsolute;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public int getxAbsolute() {
        return xAbsolute;
    }

    public int getyAbsolute() {
        return yAbsolute;
    }

    public int getToXAbsolute() {
        return toXAbsolute;
    }

    public int getToYAbsolute() {
        return toYAbsolute;
    }

    public String getScreen() {
        return screen;
    }

    public String stringify() {
        return "" +
            "{type: " + this.type + ", " +
            "label: " + this.label + ", " +
            "x: " + this.x + ", " +
            "y: " + this.y + ", " +
            "absX: " + this.xAbsolute + ", " +
            "absY: " + this.yAbsolute + ", " +
            "toX: " + this.toX + ", " +
            "toY: " + this.toY + ", " +
            "absToX: " + this.toXAbsolute + ", " +
            "absToY: " + this.toYAbsolute + ", " +
            "screen: " + this.screen + ", " +
            "timestamp: " + this.timestamp + ", " +
                "}";
    }
}
