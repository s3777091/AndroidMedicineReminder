package com.dab.medireminder.data.model;

public class HourTimer {
    private int hour;
    private int minute;
    private int positionHour;
    private int positionMinute;
    private String name;
    private boolean isAdd;

    public HourTimer () {

    }

    public HourTimer (boolean isAdd) {
        this.isAdd = isAdd;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public void setPositionHour(int positionHour) {
        this.positionHour = positionHour;
    }

    public void setPositionMinute(int positionMinute) {
        this.positionMinute = positionMinute;
    }
}
