package com.x10host.burghporter31415.TimePicker;

import java.io.Serializable;

public class DateUtil implements Serializable {

    private int year, month, dayOfMonth, hourOfDay, minute;

    public DateUtil() {/*TODO*/}

    public DateUtil(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public DateUtil(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }


    public void setDate(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public void setTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public void setYear(int year) {this.year = year;}

    public void setMonth(int month) {this.month = month;}

    public void setDayOfMonth(int dayOfMonth) {this.dayOfMonth = dayOfMonth;}

    public void setHourOfDay(int hourOfDay) {this.hourOfDay = hourOfDay;}

    public void setMinute(int minute) {this.minute = minute;}

    public int getYear() {return this.year;}

    public int getMonth() {return this.month;}

    public int getDayOfMonth() {return this.dayOfMonth;}

    public int getHourOfDay() {return this.hourOfDay;}

    public int getMinute() {return this.minute;}

    public String toString() {
        return this.year + "_" + this.month + "_" + this.dayOfMonth
                            + "_" + this.hourOfDay + "_" + this.minute + "_" + "00";
    }

}
