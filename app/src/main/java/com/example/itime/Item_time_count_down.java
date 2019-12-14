package com.example.itime;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class Item_time_count_down implements Serializable {
    private String title;
    private String message;
    private String img;
    private int year,month,day,hour,minute;
    private String repeat;
    private String label;
    private String week;
    private int CoverResourceId;

    public Item_time_count_down(String title, String message, String  img, int year, int month, int day, int hour,int minute, String week, String repeat, String label) {
        this.title = title;
        this.message = message;
        this.img = img;
        //this.CoverResourceId=CoverResourceId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour=hour;
        this.minute=minute;
        this.week=week;
        this.repeat = repeat;
        this.label = label;
    }

//    public Bitmap getImg() {
//        return img;
//    }
//
//    void setImg(Bitmap img) {
//        this.img = img;
//    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCoverResourceId() {
        return CoverResourceId;
    }

    public void setCoverResourceId(int coverResourceId) {
        CoverResourceId = coverResourceId;
    }
}
